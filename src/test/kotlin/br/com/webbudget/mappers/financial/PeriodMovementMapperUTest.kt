package br.com.webbudget.mappers.financial

import br.com.webbudget.application.mappers.financial.PeriodMovementMapper
import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapper
import br.com.webbudget.application.payloads.financial.PeriodMovementCreateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementUpdateForm
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.utilities.fixtures.createClassification
import br.com.webbudget.utilities.fixtures.createFinancialPeriod
import br.com.webbudget.utilities.fixtures.createPeriodMovement
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockKExtension::class)
class PeriodMovementMapperUTest {

    @MockK
    private lateinit var classificationRepository: ClassificationRepository

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    @MockK
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    private lateinit var periodMovementMapper: PeriodMovementMapper

    @BeforeEach
    fun setup() {

        val costCenterMapper = CostCenterMapper()

        val classificationMapper = ClassificationMapper(costCenterMapper, costCenterRepository)
        val financialPeriodMapper = FinancialPeriodMapper()

        periodMovementMapper = PeriodMovementMapper(
            classificationMapper, financialPeriodMapper, classificationRepository, financialPeriodRepository
        )
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
                assertThat(it.financialPeriod).isNotNull
                assertThat(it.classification).isNotNull
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
                assertThat(it.financialPeriod).isNotNull
            })
    }

    @Test
    fun `should map create form to domain object`() {

        val financialPeriodId = UUID.randomUUID()
        val classificationId = UUID.randomUUID()

        val classification = createClassification(externalId = classificationId)
        val financialPeriod = createFinancialPeriod(externalId = financialPeriodId)

        val form = PeriodMovementCreateForm(
            name = "Name",
            dueDate = LocalDate.now(),
            value = BigDecimal.TEN,
            financialPeriod = financialPeriodId,
            classification = classificationId,
            description = "Description"
        )

        every { classificationRepository.findByExternalId(classificationId) } returns classification
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
                assertThat(it.classification).isEqualTo(classification)
                assertThat(it.quoteNumber).isNull()
                assertThat(it.payment).isNull()
                assertThat(it.creditCardInvoice).isNull()
                assertThat(it.recurringMovement).isNull()
            })

        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationRepository, financialPeriodRepository)
    }

    @Test
    fun `should map update form to domain object`() {

        val financialPeriodId = UUID.randomUUID()
        val classificationId = UUID.randomUUID()

        val classification = createClassification(externalId = classificationId)
        val financialPeriod = createFinancialPeriod(externalId = financialPeriodId)

        val domainObject = createPeriodMovement()

        val form = PeriodMovementUpdateForm(
            name = "Name",
            dueDate = LocalDate.now(),
            value = BigDecimal.TEN,
            financialPeriod = financialPeriodId,
            classification = classificationId,
            description = "Description"
        )

        every { classificationRepository.findByExternalId(classificationId) } returns classification
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
                assertThat(it.classification).isEqualTo(classification)
                assertThat(it.quoteNumber).isNull()
                assertThat(it.payment).isNull()
                assertThat(it.creditCardInvoice).isNull()
                assertThat(it.recurringMovement).isNull()
            })

        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationRepository, financialPeriodRepository)
    }
}