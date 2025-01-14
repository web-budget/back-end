package br.com.webbudget.mappers.financial

import br.com.webbudget.application.mappers.financial.ApportionmentMapperImpl
import br.com.webbudget.application.mappers.financial.RecurringMovementMapperImpl
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.application.payloads.financial.ApportionmentForm
import br.com.webbudget.application.payloads.financial.RecurringMovementCreateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementUpdateForm
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixture.createApportionment
import br.com.webbudget.utilities.fixture.createMovementClass
import br.com.webbudget.utilities.fixture.createRecurringMovement
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
class RecurringMovementMapperUTest {

    @MockK
    private lateinit var movementClassRepository: MovementClassRepository

    private val recurringMovementMapper = RecurringMovementMapperImpl()

    @BeforeEach
    fun setUp() {
        val movementClassMapper = MovementClassMapperImpl()
        ReflectionTestUtils.setField(movementClassMapper, "costCenterMapper", CostCenterMapperImpl())

        val apportionmentMapper = ApportionmentMapperImpl()
        ReflectionTestUtils.setField(apportionmentMapper, "movementClassMapper", movementClassMapper)
        ReflectionTestUtils.setField(apportionmentMapper, "movementClassRepository", movementClassRepository)

        ReflectionTestUtils.setField(recurringMovementMapper, "apportionmentMapper", apportionmentMapper)
    }

    @Test
    fun `should map domain object to view`() {

        val domainObject = createRecurringMovement()

        val view = recurringMovementMapper.mapToView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.value).isEqualTo(domainObject.value)
                assertThat(it.startingAt).isEqualTo(domainObject.startingAt)
                assertThat(it.state).isEqualTo(domainObject.state.name)
                assertThat(it.autoLaunch).isEqualTo(domainObject.autoLaunch)
                assertThat(it.indeterminate).isEqualTo(domainObject.indeterminate)
                assertThat(it.totalQuotes).isEqualTo(domainObject.totalQuotes)
                assertThat(it.startingQuote).isEqualTo(domainObject.startingQuote)
                assertThat(it.currentQuote).isEqualTo(domainObject.currentQuote)
                assertThat(it.description).isEqualTo(domainObject.description)
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

        val domainObject = createRecurringMovement()

        val view = recurringMovementMapper.mapToListView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.value).isEqualTo(domainObject.value)
                assertThat(it.state).isEqualTo(domainObject.state.name)
                assertThat(it.autoLaunch).isEqualTo(domainObject.autoLaunch)
                assertThat(it.indeterminate).isEqualTo(domainObject.indeterminate)
                assertThat(it.totalQuotes).isEqualTo(domainObject.totalQuotes)
                assertThat(it.currentQuote).isEqualTo(domainObject.currentQuote)
            })
    }

    @Test
    fun `should map create form to domain object`() {

        val movementClassId = UUID.randomUUID()

        val movementClass = createMovementClass(externalId = movementClassId)

        val form = RecurringMovementCreateForm(
            name = "Name",
            value = BigDecimal.TEN,
            startingAt = LocalDate.now(),
            autoLaunch = true,
            indeterminate = true,
            description = "Description",
            apportionments = listOf(ApportionmentForm(BigDecimal.TEN, movementClassId))
        )

        every { movementClassRepository.findByExternalId(movementClassId) } returns movementClass

        val domainObject = recurringMovementMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.value).isEqualTo(form.value)
                assertThat(it.startingAt).isEqualTo(form.startingAt)
                assertThat(it.autoLaunch).isEqualTo(form.autoLaunch)
                assertThat(it.indeterminate).isEqualTo(form.indeterminate)
                assertThat(it.totalQuotes).isEqualTo(form.totalQuotes)
                assertThat(it.startingQuote).isEqualTo(form.startingQuote)
                assertThat(it.currentQuote).isEqualTo(form.currentQuote)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.state).isEqualTo(RecurringMovement.State.ACTIVE)
            })

        assertThat(domainObject.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.value).isEqualTo(form.apportionments!!.first().value)
                assertThat(it.movementClass).isEqualTo(movementClass)
            })

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository)
    }

    @Test
    fun `should map update form to domain object`() {

        val financialPeriodId = UUID.randomUUID()
        val movementClassId = UUID.randomUUID()

        val movementClass = createMovementClass(externalId = movementClassId)

        val domainObject = createRecurringMovement()

        val form = RecurringMovementUpdateForm(
            name = "Name",
            startingAt = LocalDate.now(),
            autoLaunch = true,
            description = "Description",
            apportionments = listOf(ApportionmentForm(BigDecimal.TEN, movementClassId))
        )

        every { movementClassRepository.findByExternalId(movementClassId) } returns movementClass

        recurringMovementMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(form.startingAt)
                assertThat(it.autoLaunch).isEqualTo(form.autoLaunch)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.state).isEqualTo(RecurringMovement.State.ACTIVE)
            })

        assertThat(domainObject.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.value).isEqualTo(form.apportionments!!.first().value)
                assertThat(it.movementClass).isEqualTo(movementClass)
            })

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository)
    }

    @Test
    fun `should map update form to domain object ignoring null values`() {

        val movementClass = createMovementClass()
        val apportionments = mutableListOf(createApportionment(movementClass = movementClass))

        val domainObject = createRecurringMovement(apportionments = apportionments)

        val form = RecurringMovementUpdateForm(name = "New name")

        recurringMovementMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(LocalDate.now())
                assertThat(it.autoLaunch).isTrue()
                assertThat(it.description).isNull()
            })

        assertThat(domainObject.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.value).isEqualTo(apportionments.first().value)
                assertThat(it.movementClass).isEqualTo(movementClass)
            })

        verify(exactly = 0) { movementClassRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository)
    }
}