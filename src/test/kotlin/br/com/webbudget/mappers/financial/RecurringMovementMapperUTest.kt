package br.com.webbudget.mappers.financial

import br.com.webbudget.application.mappers.financial.RecurringMovementMapper
import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.payloads.financial.RecurringMovementCreateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementUpdateForm
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixtures.createClassification
import br.com.webbudget.utilities.fixtures.createRecurringMovement
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockKExtension::class)
class RecurringMovementMapperUTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    @MockK
    private lateinit var classificationRepository: ClassificationRepository

    private lateinit var recurringMovementMapper: RecurringMovementMapper

    @BeforeEach
    fun setup() {
        val classificationMapper = ClassificationMapper(CostCenterMapper(), costCenterRepository)
        recurringMovementMapper = RecurringMovementMapper(classificationMapper, classificationRepository)
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
                assertThat(it.classification).isNotNull
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

        val classificationId = UUID.randomUUID()
        val classification = createClassification(externalId = classificationId)

        val form = RecurringMovementCreateForm(
            name = "Name",
            value = BigDecimal.TEN,
            startingAt = LocalDate.now(),
            autoLaunch = true,
            indeterminate = true,
            description = "Description",
            classification = classificationId,
        )

        every { classificationRepository.findByExternalId(classificationId) } returns classification

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
                assertThat(it.classification).isEqualTo(classification)
            })

        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationRepository)
    }

    @Test
    fun `should map update form to domain object`() {

        val classificationId = UUID.randomUUID()
        val classification = createClassification(externalId = classificationId)

        val domainObject = createRecurringMovement()

        val form = RecurringMovementUpdateForm(
            name = "Name",
            startingAt = LocalDate.now(),
            autoLaunch = true,
            description = "Description",
            classification = classificationId
        )

        every { classificationRepository.findByExternalId(classificationId) } returns classification

        recurringMovementMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(form.startingAt)
                assertThat(it.autoLaunch).isEqualTo(form.autoLaunch)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.state).isEqualTo(RecurringMovement.State.ACTIVE)
                assertThat(it.classification).isEqualTo(classification)
            })

        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationRepository)
    }
}