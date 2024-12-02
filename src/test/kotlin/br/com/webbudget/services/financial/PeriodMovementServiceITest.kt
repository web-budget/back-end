package br.com.webbudget.services.financial

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.services.financial.PeriodMovementService
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixture.createApportionment
import br.com.webbudget.utilities.fixture.createPeriodMovement
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.UUID

class PeriodMovementServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @Autowired
    private lateinit var movementClassRepository: MovementClassRepository

    @Autowired
    private lateinit var periodMovementRepository: PeriodMovementRepository

    @Autowired
    private lateinit var periodMovementService: PeriodMovementService

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should create period movement`() {

        val financialPeriodId = UUID.fromString("27881a12-5e61-43cd-a6d0-fdb32eaa75c0")
        val movementClassId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val financialPeriod = financialPeriodRepository.findByExternalId(financialPeriodId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val movementClass = movementClassRepository.findByExternalId(movementClassId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val apportionments = mutableListOf(
            createApportionment(value = BigDecimal("10.99"), movementClass = movementClass)
        )

        val periodMovement = createPeriodMovement(
            value = BigDecimal("10.99"),
            apportionments = apportionments,
            financialPeriod = financialPeriod
        )

        val externalId = periodMovementService.create(periodMovement)

        val saved = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(saved).satisfies({
            assertThat(it.id).isNotNull()
            assertThat(it.externalId).isEqualTo(externalId)
            assertThat(it.version).isZero()
            assertThat(it.createdOn).isNotNull()
            assertThat(it.lastUpdate).isNotNull()
            assertThat(it.name).isEqualTo(periodMovement.name)
            assertThat(it.dueDate).isEqualTo(periodMovement.dueDate)
            assertThat(it.value).isEqualTo(periodMovement.value)
            assertThat(it.financialPeriod).isEqualTo(periodMovement.financialPeriod)
            assertThat(it.state).isEqualTo(periodMovement.state)
            assertThat(it.quoteNumber).isEqualTo(periodMovement.quoteNumber)
            assertThat(it.description).isEqualTo(periodMovement.description)
            assertThat(it.payment).isEqualTo(periodMovement.payment)
            assertThat(it.creditCardInvoice).isEqualTo(periodMovement.creditCardInvoice)
            assertThat(it.recurringMovement).isEqualTo(periodMovement.recurringMovement)
        })

        assertThat(periodMovement.apportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.id).isNotNull()
                assertThat(it.externalId).isNotNull()
                assertThat(it.version).isZero()
                assertThat(it.createdOn).isNotNull()
                assertThat(it.lastUpdate).isNotNull()
                assertThat(it.value).isEqualTo(BigDecimal("10.99"))
                assertThat(it.movementClass).isEqualTo(movementClass)
            })
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to create if apportionments total is not equal to movement total`() {

        val financialPeriodId = UUID.fromString("27881a12-5e61-43cd-a6d0-fdb32eaa75c0")
        val movementClassId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val financialPeriod = financialPeriodRepository.findByExternalId(financialPeriodId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val movementClass = movementClassRepository.findByExternalId(movementClassId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val apportionments = mutableListOf(
            createApportionment(value = BigDecimal("5.99"), movementClass = movementClass)
        )

        val periodMovement = createPeriodMovement(
            value = BigDecimal("10.99"),
            apportionments = apportionments,
            financialPeriod = financialPeriod
        )

        assertThatThrownBy { periodMovementService.create(periodMovement) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to create if financial period is not open`() {

        val financialPeriodId = UUID.fromString("df05156c-3fce-4f56-88ff-918d50200312")
        val movementClassId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val financialPeriod = financialPeriodRepository.findByExternalId(financialPeriodId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val movementClass = movementClassRepository.findByExternalId(movementClassId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val apportionments = mutableListOf(
            createApportionment(value = BigDecimal("10.99"), movementClass = movementClass)
        )

        val periodMovement = createPeriodMovement(
            value = BigDecimal("10.99"),
            apportionments = apportionments,
            financialPeriod = financialPeriod
        )

        assertThatThrownBy { periodMovementService.create(periodMovement) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/financial/create-period-movement.sql",
    )
    fun `should update period movement`() {

        val externalId = UUID.fromString("287e26fa-763b-4efb-908e-734e637bb6fd")

        // TODO make checks here
    }

    @Test
    fun `should fail to update if apportionments total is not equal to movement total`() {
    }

    @Test
    fun `should fail to update if financial period is not open`() {
    }

    @Test
    fun `should fail to update if movement is accounted`() {
    }

    @Test
    fun `should delete period movement`() {
    }

    @Test
    fun `should fail to delete if movement is accounted`() {
    }
}