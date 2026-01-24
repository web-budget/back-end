package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.payloads.registration.ClassificationCreateForm
import br.com.webbudget.application.payloads.registration.ClassificationUpdateForm
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.entities.registration.Classification.Type.EXPENSE
import br.com.webbudget.domain.entities.registration.Classification.Type.INCOME
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixtures.createClassification
import br.com.webbudget.utilities.fixtures.createCostCenter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ClassificationMapperUTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    private lateinit var classificationMapper: ClassificationMapper

    @BeforeEach
    fun setup() {
        classificationMapper = ClassificationMapper(CostCenterMapper(), costCenterRepository)
    }

    @ParameterizedTest
    @MethodSource("createFormObjects")
    fun `should map create form to domain object`(form: ClassificationCreateForm) {

        every { costCenterRepository.findByExternalId(any<UUID>()) } answers {
            createCostCenter(externalId = form.costCenter)
        }

        val domainObject = classificationMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.type).isEqualTo(form.type)
                assertThat(it.budget).isEqualTo(form.budget)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.costCenter)
                    .satisfies({ costCenter -> assertThat(costCenter.externalId).isEqualTo(form.costCenter) })
            })

        verify(exactly = 1) { costCenterRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(costCenterRepository)
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createClassification()
        val form = ClassificationUpdateForm("Expenses", UUID.randomUUID(), BigDecimal.TEN, "Description", false)

        every { costCenterRepository.findByExternalId(any<UUID>()) } answers {
            createCostCenter(externalId = form.costCenter)
        }

        classificationMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.budget).isEqualTo(form.budget)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.costCenter)
                    .satisfies({ costCenter ->
                        assertThat(costCenter.externalId).isEqualTo(form.costCenter)
                    })
            })

        verify(exactly = 1) { costCenterRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(costCenterRepository)
    }

    @ParameterizedTest
    @MethodSource("domainObjects")
    fun `should map domain object to view`(domainObject: Classification) {

        val externalId = UUID.randomUUID()

        domainObject.apply {
            this.id = 1L
            this.externalId = externalId
        }

        val view = classificationMapper.mapToView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId!!)
                assertThat(it.name).isEqualTo(view.name)
                assertThat(it.type).isEqualTo(view.type)
                assertThat(it.budget).isEqualTo(view.budget)
                assertThat(it.description).isEqualTo(view.description)
                assertThat(it.active).isTrue()
            })
    }

    @ParameterizedTest
    @MethodSource("domainObjects")
    fun `should map domain object to list view`(domainObject: Classification) {

        val externalId = UUID.randomUUID()

        domainObject.apply {
            this.id = 1L
            this.externalId = externalId
        }

        val view = classificationMapper.mapToListView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId!!)
                assertThat(it.name).isEqualTo(view.name)
                assertThat(it.type).isEqualTo(view.type)
                assertThat(it.active).isTrue()
            })
    }

    companion object {

        @JvmStatic
        fun createFormObjects() = listOf(
            Arguments.of(ClassificationCreateForm("Income", INCOME, UUID.randomUUID(), BigDecimal.ONE, "Description")),
            Arguments.of(ClassificationCreateForm("Expense", EXPENSE, UUID.randomUUID(), BigDecimal.ONE, "Description"))
        )

        @JvmStatic
        fun domainObjects() = listOf(
            Arguments.of(createClassification(name = "Expenses", type = EXPENSE)),
            Arguments.of(createClassification(name = "Incomes", type = INCOME))
        )
    }
}
