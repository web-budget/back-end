package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.payloads.registration.CostCenterCreateForm
import br.com.webbudget.application.payloads.registration.CostCenterListView
import br.com.webbudget.application.payloads.registration.CostCenterUpdateForm
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixtures.createCostCenter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class CostCenterMapperUTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    @InjectMockKs
    private lateinit var costCenterMapper: CostCenterMapper

    @Test
    fun `should map create form to domain object`() {

        val form = CostCenterCreateForm(
            name = "Cost Center",
            description = "Some cost center",
            incomeBudget = BigDecimal.ONE,
            expenseBudget = BigDecimal.ONE
        )

        val domainObject = costCenterMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.incomeBudget).isEqualTo(form.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(form.expenseBudget)
            })
    }

    @Test
    fun `should map create form with parent to domain object with parent`() {

        val parentCostCenterId = UUID.randomUUID()
        val parentCostCenter = createCostCenter(externalId = parentCostCenterId)

        val form = CostCenterCreateForm(
            name = "Cost Center",
            description = "Some cost center",
            incomeBudget = BigDecimal.ONE,
            expenseBudget = BigDecimal.ONE,
            parentCostCenter = parentCostCenterId
        )

        every { costCenterRepository.findByExternalId(eq(parentCostCenterId)) } returns parentCostCenter

        val domainObject = costCenterMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.incomeBudget).isEqualTo(form.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(form.expenseBudget)
                assertThat(it.parent).isNotNull
            })

        verify(exactly = 1) { costCenterRepository.findByExternalId(eq(parentCostCenterId)) }

        confirmVerified(costCenterRepository)
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createCostCenter()
        val form = CostCenterUpdateForm("Other", false, "Other", BigDecimal.ONE, BigDecimal.ONE)

        costCenterMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.incomeBudget).isEqualTo(form.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(form.expenseBudget)
                assertThat(it.active).isEqualTo(form.active)
            })
    }

    @Test
    fun `should map update form with parent to domain object with parent`() {

        val parentCostCenterId = UUID.randomUUID()
        val parentCostCenter = createCostCenter(externalId = parentCostCenterId)

        val domainObject = createCostCenter(parentCostCenter = null)
        val form = CostCenterUpdateForm("Other", false, "Other", BigDecimal.ONE, BigDecimal.ONE, parentCostCenterId)

        every { costCenterRepository.findByExternalId(eq(parentCostCenterId)) } returns parentCostCenter

        costCenterMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.incomeBudget).isEqualTo(form.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(form.expenseBudget)
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.parent).isEqualTo(parentCostCenter)
            })

        verify(exactly = 1) { costCenterRepository.findByExternalId(eq(parentCostCenterId)) }

        confirmVerified(costCenterRepository)
    }

    @ParameterizedTest
    @MethodSource("costCenterAndParents")
    fun `should map domain object to view`(
        domainObject: CostCenter,
        externalId: UUID,
        extraAssert: (CostCenterView) -> Unit
    ) {
        val view = costCenterMapper.mapToView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(externalId)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.incomeBudget).isEqualTo(domainObject.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(domainObject.expenseBudget)
                assertThat(it.description).isEqualTo(domainObject.description)
            })

        extraAssert(view)
    }

    @ParameterizedTest
    @MethodSource("costCenterAndParentsNaming")
    fun `should map domain object to list view`(
        domainObject: CostCenter,
        externalId: UUID,
        extraAssert: (CostCenterListView) -> Unit
    ) {
        val view = costCenterMapper.mapToListView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(externalId)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.incomeBudget).isEqualTo(domainObject.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(domainObject.expenseBudget)
            })

        extraAssert(view)
    }

    companion object {

        @JvmStatic
        fun costCenterAndParentsNaming(): Stream<Arguments> {

            val domainObjectWithoutParentExternalId = UUID.randomUUID()
            val domainObjectWithoutParent = createCostCenter(externalId = domainObjectWithoutParentExternalId)

            val parentCostCenter = createCostCenter(externalId = UUID.randomUUID())

            val domainObjectWithParentExternalId = UUID.randomUUID()
            val domainObjectWithParent = createCostCenter(
                externalId = domainObjectWithParentExternalId,
                parentCostCenter = parentCostCenter
            )

            val assertParentNaming = { view: CostCenterListView ->
                assertThat(view.name).isEqualTo("${parentCostCenter.name} > ${domainObjectWithParent.name}")
            }
            val assertNamingWithoutParent = { view: CostCenterListView ->
                assertThat(view.name).isEqualTo(domainObjectWithoutParent.name)
            }

            return Stream.of(
                Arguments.of(domainObjectWithParent, domainObjectWithParentExternalId, assertParentNaming),
                Arguments.of(domainObjectWithoutParent, domainObjectWithoutParentExternalId, assertNamingWithoutParent)
            )
        }

        @JvmStatic
        fun costCenterAndParents(): Stream<Arguments> {

            val domainObjectWithoutParentExternalId = UUID.randomUUID()
            val domainObjectWithoutParent = createCostCenter(externalId = domainObjectWithoutParentExternalId)

            val parentCostCenter = createCostCenter(externalId = UUID.randomUUID())

            val domainObjectWithParentExternalId = UUID.randomUUID()
            val domainObjectWithParent = createCostCenter(
                externalId = domainObjectWithParentExternalId,
                parentCostCenter = parentCostCenter
            )

            val parentAssertNotNull = { view: CostCenterView ->
                assertThat(view.parentCostCenter!!)
                    .isNotNull
                    .satisfies({
                        assertThat(it.id).isEqualTo(parentCostCenter.externalId)
                        assertThat(it.name).isEqualTo(parentCostCenter.name)
                    })
            }
            val parentAssertIsNull = { view: CostCenterView -> assertThat(view.parentCostCenter).isNull() }

            return Stream.of(
                Arguments.of(domainObjectWithParent, domainObjectWithParentExternalId, parentAssertNotNull),
                Arguments.of(domainObjectWithoutParent, domainObjectWithoutParentExternalId, parentAssertIsNull)
            )
        }
    }
}
