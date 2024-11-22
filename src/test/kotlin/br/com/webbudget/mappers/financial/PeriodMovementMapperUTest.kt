package br.com.webbudget.mappers.financial

import br.com.webbudget.application.mappers.financial.ApportionmentMapperImpl
import br.com.webbudget.application.mappers.financial.PeriodMovementMapperImpl
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.application.payloads.financial.ApportionmentForm
import br.com.webbudget.application.payloads.financial.PeriodMovementCreateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementUpdateForm
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixture.createApportionment
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import br.com.webbudget.utilities.fixture.createMovementClass
import br.com.webbudget.utilities.fixture.createPeriodMovement
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockKExtension::class)
class PeriodMovementMapperUTest {

    @MockK
    private lateinit var movementClassRepository: MovementClassRepository

    @MockK
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    private val periodMovementMapper = PeriodMovementMapperImpl()

    @BeforeEach
    fun setUp() {
        val movementClassMapper = MovementClassMapperImpl()
        ReflectionTestUtils.setField(movementClassMapper, "costCenterMapper", CostCenterMapperImpl())

        val apportionmentMapper = ApportionmentMapperImpl()
        ReflectionTestUtils.setField(apportionmentMapper, "movementClassMapper", movementClassMapper)
        ReflectionTestUtils.setField(apportionmentMapper, "movementClassRepository", movementClassRepository)

        ReflectionTestUtils.setField(periodMovementMapper, "apportionmentMapper", apportionmentMapper)
        ReflectionTestUtils.setField(periodMovementMapper, "financialPeriodMapper", FinancialPeriodMapperImpl())

        ReflectionTestUtils.setField(periodMovementMapper, "financialPeriodRepository", financialPeriodRepository)
    }

    @Test
    fun `should map domain object to view`() {

        val domainObject = createPeriodMovement()

        val view = periodMovementMapper.mapToView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.dueDate).isEqualTo(domainObject.dueDate)
                assertThat(it.value).isEqualTo(domainObject.value)
                assertThat(it.state).isEqualTo(domainObject.state.name)
                assertThat(it.quoteNumber).isEqualTo(domainObject.quoteNumber)
                assertThat(it.description).isEqualTo(domainObject.description)
            })

        assertThat(view.financialPeriod)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.financialPeriod.externalId)
                assertThat(it.name).isEqualTo(domainObject.financialPeriod.name)
                assertThat(it.startingAt).isEqualTo(domainObject.financialPeriod.startingAt)
                assertThat(it.endingAt).isEqualTo(domainObject.financialPeriod.endingAt)
                assertThat(it.status).isEqualTo(domainObject.financialPeriod.status.name)
            })

        assertThat(view.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.id).isEqualTo(domainObject.apportionments.first().externalId)
                assertThat(it.value).isEqualTo(domainObject.apportionments.first().value)
                assertThat(it.movementClass).satisfies({ mc ->
                    assertThat(mc.id).isEqualTo(domainObject.apportionments.first().movementClass.externalId)
                    assertThat(mc.name).isEqualTo(domainObject.apportionments.first().movementClass.name)
                    assertThat(mc.type).isEqualTo(domainObject.apportionments.first().movementClass.type.name)
                    assertThat(mc.active).isEqualTo(domainObject.apportionments.first().movementClass.active)
                })
                assertThat(it.movementClass.costCenter).satisfies({ mc ->
                    assertThat(mc.id).isEqualTo(domainObject.apportionments.first().movementClass.costCenter.externalId)
                    assertThat(mc.name).isEqualTo(domainObject.apportionments.first().movementClass.costCenter.name)
                    assertThat(mc.active).isEqualTo(domainObject.apportionments.first().movementClass.costCenter.active)
                })
            })
    }

    @Test
    fun `should map domain object to list view`() {

        val domainObject = createPeriodMovement()

        val view = periodMovementMapper.mapToListView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.dueDate).isEqualTo(domainObject.dueDate)
                assertThat(it.value).isEqualTo(domainObject.value)
                assertThat(it.state).isEqualTo(domainObject.state.name)
            })

        assertThat(view.financialPeriod)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.financialPeriod.externalId)
                assertThat(it.name).isEqualTo(domainObject.financialPeriod.name)
                assertThat(it.startingAt).isEqualTo(domainObject.financialPeriod.startingAt)
                assertThat(it.endingAt).isEqualTo(domainObject.financialPeriod.endingAt)
                assertThat(it.status).isEqualTo(domainObject.financialPeriod.status.name)
            })
    }

    @Test
    fun `should map create form to domain object`() {

        val financialPeriodId = UUID.randomUUID()
        val movementClassId = UUID.randomUUID()

        val movementClass = createMovementClass(externalId = movementClassId)
        val financialPeriod = createFinancialPeriod(externalId = financialPeriodId)

        val form = PeriodMovementCreateForm(
            "Name",
            LocalDate.now(),
            BigDecimal.TEN,
            financialPeriodId,
            "Description",
            listOf(ApportionmentForm(BigDecimal.TEN, movementClassId))
        )

        every { movementClassRepository.findByExternalId(movementClassId) } returns movementClass
        every { financialPeriodRepository.findByExternalId(financialPeriodId) } returns financialPeriod

        val domainObject = periodMovementMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.dueDate).isEqualTo(form.dueDate)
                assertThat(it.value).isEqualTo(form.value)
                assertThat(it.state).isEqualTo(PeriodMovement.State.OPEN)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.financialPeriod).isEqualTo(financialPeriod)
                assertThat(it.quoteNumber).isNull()
                assertThat(it.payment).isNull()
                assertThat(it.creditCardInvoice).isNull()
                assertThat(it.recurringMovement).isNull()
                assertThat(it.apportionments).isNotEmpty().hasSize(1)
            })

        assertThat(domainObject.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.value).isEqualTo(form.apportionments!!.first().value)
                assertThat(it.movementClass).isEqualTo(movementClass)
            })

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository, financialPeriodRepository)
    }

    @Test
    fun `should map update form to domain object`() {

        val financialPeriodId = UUID.randomUUID()
        val movementClassId = UUID.randomUUID()

        val movementClass = createMovementClass(externalId = movementClassId)
        val financialPeriod = createFinancialPeriod(externalId = financialPeriodId)

        val domainObject = createPeriodMovement()

        val form = PeriodMovementUpdateForm(
            "Name",
            LocalDate.now(),
            BigDecimal.TEN,
            financialPeriodId,
            "Description",
            listOf(ApportionmentForm(BigDecimal.TEN, movementClassId))
        )

        every { movementClassRepository.findByExternalId(movementClassId) } returns movementClass
        every { financialPeriodRepository.findByExternalId(financialPeriodId) } returns financialPeriod

        periodMovementMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.dueDate).isEqualTo(form.dueDate)
                assertThat(it.value).isEqualTo(form.value)
                assertThat(it.state).isEqualTo(PeriodMovement.State.OPEN)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.financialPeriod).isEqualTo(financialPeriod)
                assertThat(it.quoteNumber).isNull()
                assertThat(it.payment).isNull()
                assertThat(it.creditCardInvoice).isNull()
                assertThat(it.recurringMovement).isNull()
                assertThat(it.apportionments).isNotEmpty().hasSize(1)
            })

        assertThat(domainObject.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.value).isEqualTo(form.apportionments!!.first().value)
                assertThat(it.movementClass).isEqualTo(movementClass)
            })

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository, financialPeriodRepository)
    }

    @Test
    fun `should map update form to domain object ignoring null values`() {

        val movementClass = createMovementClass()
        val financialPeriod = createFinancialPeriod()
        val apportionments = mutableListOf(createApportionment(movementClass = movementClass))

        val domainObject = createPeriodMovement(financialPeriod = financialPeriod, apportionments = apportionments)

        val form = PeriodMovementUpdateForm(name = "New name")

        periodMovementMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo("New name")
                assertThat(it.dueDate).isEqualTo(LocalDate.now())
                assertThat(it.value).isEqualTo(BigDecimal.ONE)
                assertThat(it.state).isEqualTo(PeriodMovement.State.OPEN)
                assertThat(it.description).isNull()
                assertThat(it.financialPeriod).isEqualTo(financialPeriod)
                assertThat(it.quoteNumber).isNull()
                assertThat(it.payment).isNull()
                assertThat(it.creditCardInvoice).isNull()
                assertThat(it.recurringMovement).isNull()
            })

        assertThat(domainObject.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.value).isEqualTo(apportionments.first().value)
                assertThat(it.movementClass).isEqualTo(movementClass)
            })

        verify(exactly = 0) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 0) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository, financialPeriodRepository)
    }
}