package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.services.registration.FinancialPeriodService
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class FinancialPeriodServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @Autowired
    private lateinit var financialPeriodService: FinancialPeriodService

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should create`() {

        val financialPeriod = createFinancialPeriod()

        val externalId = financialPeriodService.create(financialPeriod)

        val saved = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(saved).satisfies({
            assertThat(it.id).isNotNull()
            assertThat(it.externalId).isEqualTo(externalId)
            assertThat(it.version).isZero()
            assertThat(it.createdOn).isNotNull()
            assertThat(it.lastUpdate).isNotNull()
            assertThat(it.status).isEqualTo(financialPeriod.status)
            assertThat(it.name).isEqualTo(financialPeriod.name)
            assertThat(it.startingAt).isEqualTo(financialPeriod.startingAt)
            assertThat(it.endingAt).isEqualTo(financialPeriod.endingAt)
        })
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to create if name is duplicated`() {

        val financialPeriod = createFinancialPeriod(
            name = "08/2024",
            startingAt = LocalDate.of(2024, 9, 1),
            endingAt = LocalDate.of(2024, 9, 30)
        )

        assertThatThrownBy { financialPeriodService.create(financialPeriod) }
            .isInstanceOf(ConflictingPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to create if periods overlap`() {

        val start = LocalDate.of(2024, 8, 1)
        val end = start.plusDays(15)

        val financialPeriod = createFinancialPeriod(name = "Agosto", startingAt = start, endingAt = end)

        assertThatThrownBy { financialPeriodService.create(financialPeriod) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should fail to create if start date is after end date`() {

        val financialPeriod = createFinancialPeriod(
            name = "09/2024",
            startingAt = LocalDate.of(2024, 9, 30),
            endingAt = LocalDate.of(2024, 9, 1)
        )

        assertThatThrownBy { financialPeriodService.create(financialPeriod) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Start date must be before end date")
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should update`() {

        val externalId = UUID.fromString("27881a12-5e61-43cd-a6d0-fdb32eaa75c0")
        val toUpdate = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "09/2024"
            this.startingAt = LocalDate.of(2024, 9, 1)
            this.endingAt = LocalDate.of(2024, 9, 30)
            this.revenuesGoal = BigDecimal("190.83")
            this.expensesGoal = BigDecimal("250.29")
            this.status = FinancialPeriod.Status.ENDED
        }

        financialPeriodService.update(toUpdate)

        val updated = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(updated).satisfies({
            assertThat(it.id).isNotNull()
            assertThat(it.externalId).isEqualTo(externalId)
            assertThat(it.version).isGreaterThan(toUpdate.version)
            assertThat(it.createdOn).isEqualTo(toUpdate.createdOn)
            assertThat(it.lastUpdate).isAfter(toUpdate.lastUpdate)
            assertThat(it.status).isEqualTo(toUpdate.status)
            assertThat(it.name).isEqualTo(toUpdate.name)
            assertThat(it.startingAt).isEqualTo(toUpdate.startingAt)
            assertThat(it.endingAt).isEqualTo(toUpdate.endingAt)
            assertThat(it.revenuesGoal).isEqualTo(toUpdate.revenuesGoal)
            assertThat(it.expensesGoal).isEqualTo(toUpdate.expensesGoal)
        })
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to update if name is duplicated`() {

        val financialPeriod = createFinancialPeriod(
            name = "10/2024",
            startingAt = LocalDate.of(2024, 10, 1),
            endingAt = LocalDate.of(2024, 10, 31)
        )

        val externalId = financialPeriodService.create(financialPeriod)
        val toUpdate = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "08/2024"
        }

        assertThatThrownBy { financialPeriodService.update(toUpdate) }
            .isInstanceOf(ConflictingPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to update if periods overlap`() {

        val financialPeriod = createFinancialPeriod(
            name = "10/2024",
            startingAt = LocalDate.of(2024, 10, 1),
            endingAt = LocalDate.of(2024, 10, 31)
        )

        val externalId = financialPeriodService.create(financialPeriod)
        val toUpdate = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.startingAt = LocalDate.of(2024, 8, 1)
            this.endingAt = LocalDate.of(2024, 8, 31)
        }

        assertThatThrownBy { financialPeriodService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should fail to update if start date is after end date`() {

        val financialPeriod = createFinancialPeriod(
            name = "10/2024",
            startingAt = LocalDate.of(2024, 10, 1),
            endingAt = LocalDate.of(2024, 10, 31)
        )

        val externalId = financialPeriodService.create(financialPeriod)
        val toUpdate = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.endingAt = LocalDate.of(2024, 10, 5)
            this.startingAt = LocalDate.of(2024, 10, 10)
        }

        assertThatThrownBy { financialPeriodService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Start date must be before end date")
    }

    @Test
    fun `should fail to update if period is not open`() {

        val financialPeriod = createFinancialPeriod(
            name = "10/2024",
            startingAt = LocalDate.of(2024, 10, 1),
            endingAt = LocalDate.of(2024, 10, 31),
            status = FinancialPeriod.Status.ACCOUNTED
        )

        val externalId = financialPeriodService.create(financialPeriod)
        val toUpdate = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThatThrownBy { financialPeriodService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("You can't delete or update non open periods")
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should delete`() {

        val externalId = UUID.fromString("27881a12-5e61-43cd-a6d0-fdb32eaa75c0")
        val toDelete = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        financialPeriodService.delete(toDelete)

        val deleted = financialPeriodRepository.findByExternalId(externalId)
        assertThat(deleted).isNull()
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-financial-period.sql"
    )
    fun `should fail to delete if period is not open`() {

        val externalId = UUID.fromString("27881a12-5e61-43cd-a6d0-fdb32eaa75c0")
        val toUpdate = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.status = FinancialPeriod.Status.ACCOUNTED
        }

        financialPeriodService.update(toUpdate)

        val toDelete = financialPeriodRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThatThrownBy { financialPeriodService.delete(toDelete) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("You can't delete or update non open periods")
    }

    @Test
    @Disabled
    fun `should fail to delete if period has movements`() {
        // TODO this should be done after the movement feature is created
    }
}