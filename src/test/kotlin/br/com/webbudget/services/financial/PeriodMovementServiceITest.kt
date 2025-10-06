package br.com.webbudget.services.financial

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.services.financial.PeriodMovementService
import br.com.webbudget.infrastructure.repository.financial.ApportionmentRepository
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixtures.createApportionment
import br.com.webbudget.utilities.fixtures.createPeriodMovement
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class PeriodMovementServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var apportionmentRepository: ApportionmentRepository

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
        "/sql/registration/create-financial-period.sql",
        "/sql/financial/create-period-movement.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should update period movement`() {

        val externalId = UUID.fromString("287e26fa-763b-4efb-908e-734e637bb6fd")

        val toUpdate = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        val movementClassId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val movementClass = movementClassRepository.findByExternalId(movementClassId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val apportionments = mutableListOf(
            createApportionment(value = BigDecimal("111.50"), movementClass = movementClass)
        )

        toUpdate.apply {
            this.name = "Updated"
            this.value = BigDecimal("111.50")
            this.dueDate = LocalDate.of(2025, 1, 1)
            this.state = PeriodMovement.State.PAID
        }

        toUpdate.setApportionments(apportionments)

        periodMovementService.update(toUpdate)

        val updated = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(updated).satisfies({
            assertThat(it.id).isEqualTo(toUpdate.id)
            assertThat(it.externalId).isEqualTo(toUpdate.externalId)
            assertThat(it.version).isGreaterThan(toUpdate.version)
            assertThat(it.createdOn).isEqualTo(toUpdate.createdOn)
            assertThat(it.lastUpdate).isAfter(toUpdate.lastUpdate)
            assertThat(it.name).isEqualTo(toUpdate.name)
            assertThat(it.dueDate).isEqualTo(toUpdate.dueDate)
            assertThat(it.value).isEqualTo(toUpdate.value)
            assertThat(it.financialPeriod).isEqualTo(toUpdate.financialPeriod)
            assertThat(it.state).isEqualTo(toUpdate.state)
            assertThat(it.quoteNumber).isEqualTo(toUpdate.quoteNumber)
            assertThat(it.description).isEqualTo(toUpdate.description)
            assertThat(it.payment).isEqualTo(toUpdate.payment)
            assertThat(it.creditCardInvoice).isEqualTo(toUpdate.creditCardInvoice)
            assertThat(it.recurringMovement).isEqualTo(toUpdate.recurringMovement)
        })

        val updatedApportionments = apportionmentRepository.findByPeriodMovementExternalId(externalId)

        assertThat(updatedApportionments)
            .isNotEmpty
            .hasSize(1)
            .satisfiesExactlyInAnyOrder({
                assertThat(it.id).isNotNull()
                assertThat(it.externalId).isNotNull()
                assertThat(it.version).isZero()
                assertThat(it.createdOn).isNotNull()
                assertThat(it.lastUpdate).isNotNull()
                assertThat(it.value).isEqualTo(BigDecimal("111.50"))
                assertThat(it.movementClass).isEqualTo(movementClass)
            })
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql",
        "/sql/financial/create-period-movement.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should fail to update if apportionments total is not equal to movement total`() {

        val externalId = UUID.fromString("287e26fa-763b-4efb-908e-734e637bb6fd")

        val toUpdate = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        val movementClassId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val movementClass = movementClassRepository.findByExternalId(movementClassId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val apportionments = mutableListOf(
            createApportionment(value = BigDecimal("111.50"), movementClass = movementClass)
        )

        toUpdate.apply {
            this.name = "Updated"
            this.value = BigDecimal("111.49")
            this.dueDate = LocalDate.of(2025, 1, 1)
            this.state = PeriodMovement.State.PAID
        }

        toUpdate.setApportionments(apportionments)

        assertThatThrownBy { periodMovementService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql",
        "/sql/financial/create-period-movement.sql"
    )
    fun `should fail to update if financial period is not open`() {

        val externalId = UUID.fromString("287e26fa-763b-4efb-908e-734e637bb6fd")

        val toUpdate = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        val financialPeriodId = UUID.fromString("df05156c-3fce-4f56-88ff-918d50200312")
        val financialPeriod = financialPeriodRepository.findByExternalId(financialPeriodId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.financialPeriod = financialPeriod
        }

        assertThatThrownBy { periodMovementService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql",
        "/sql/financial/create-period-movement.sql"
    )
    fun `should fail to update if movement is accounted`() {

        val externalId = UUID.fromString("413b96ec-fed9-487f-b587-7bb9c4989020")

        val toUpdate = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Updated"
        }

        assertThatThrownBy { periodMovementService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql",
        "/sql/financial/create-period-movement.sql"
    )
    fun `should delete period movement`() {

        val externalId = UUID.fromString("287e26fa-763b-4efb-908e-734e637bb6fd")

        val toDelete = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        periodMovementService.delete(toDelete)

        val deleted = periodMovementRepository.findByExternalId(externalId)

        assertThat(deleted).isNull()
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql",
        "/sql/financial/create-period-movement.sql"
    )
    fun `should fail to delete if movement is accounted`() {

        val externalId = UUID.fromString("413b96ec-fed9-487f-b587-7bb9c4989020")

        val toDelete = periodMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThatThrownBy { periodMovementService.delete(toDelete) }
            .isInstanceOf(BusinessException::class.java)
    }
}