package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.application.payloads.registration.MovementClassCreateForm
import br.com.webbudget.application.payloads.registration.MovementClassUpdateForm
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.entities.registration.MovementClass.Type.EXPENSE
import br.com.webbudget.domain.entities.registration.MovementClass.Type.INCOME
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixture.createCostCenter
import br.com.webbudget.utilities.fixture.createMovementClass
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
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockKExtension::class)
class MovementClassMapperUTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    private val movementClassMapper = MovementClassMapperImpl()

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(movementClassMapper, "costCenterMapper", CostCenterMapperImpl())
        ReflectionTestUtils.setField(movementClassMapper, "costCenterRepository", costCenterRepository)
    }

    @ParameterizedTest
    @MethodSource("createFormObjects")
    fun `should map create form to domain object`(form: MovementClassCreateForm) {

        every { costCenterRepository.findByExternalId(any<UUID>()) } answers {
            createCostCenter(externalId = form.costCenter)
        }

        val domainObject = movementClassMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.type).isEqualTo(form.type)
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

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createMovementClass()
        val form = MovementClassUpdateForm("Expenses", UUID.randomUUID(), BigDecimal.TEN, "Description", false)

        every { costCenterRepository.findByExternalId(any<UUID>()) } answers {
            createCostCenter(externalId = form.costCenter)
        }

        movementClassMapper.map(form, domainObject)

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
    fun `should map domain object to view`(domainObject: MovementClass) {

        val externalId = UUID.randomUUID()

        domainObject.apply {
            this.id = 1L
            this.externalId = externalId
        }

        val view = movementClassMapper.map(domainObject)

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

    companion object {

        @JvmStatic
        fun createFormObjects() = listOf(
            Arguments.of(MovementClassCreateForm("Income", INCOME, UUID.randomUUID(), BigDecimal.ONE, "Description")),
            Arguments.of(MovementClassCreateForm("Expense", EXPENSE, UUID.randomUUID(), BigDecimal.ONE, "Description"))
        )

        @JvmStatic
        fun domainObjects() = listOf(
            Arguments.of(createMovementClass(name = "Expenses", type = EXPENSE)),
            Arguments.of(createMovementClass(name = "Incomes", type = INCOME))
        )
    }
}
