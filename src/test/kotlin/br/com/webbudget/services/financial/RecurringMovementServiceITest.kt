package br.com.webbudget.services.financial

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.services.financial.RecurringMovementService
import br.com.webbudget.infrastructure.repository.financial.RecurringMovementRepository
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.utilities.fixtures.createRecurringMovement
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

class RecurringMovementServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var classificationRepository: ClassificationRepository

    @Autowired
    private lateinit var recurringMovementRepository: RecurringMovementRepository

    @Autowired
    private lateinit var recurringMovementService: RecurringMovementService

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    fun `should create period movement`() {

        val classificationId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val classification = classificationRepository.findByExternalId(classificationId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val recurringMovement = createRecurringMovement(
            value = BigDecimal("10.99"),
            classification = classification
        )

        val externalId = recurringMovementService.create(recurringMovement)

        val saved = recurringMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(saved).satisfies({
            assertThat(it.id).isNotNull()
            assertThat(it.externalId).isEqualTo(externalId)
            assertThat(it.version).isZero()
            assertThat(it.createdOn).isNotNull()
            assertThat(it.lastUpdate).isNotNull()
            assertThat(it.name).isEqualTo(recurringMovement.name)
            assertThat(it.value).isEqualTo(recurringMovement.value)
            assertThat(it.startingAt).isEqualTo(recurringMovement.startingAt)
            assertThat(it.state).isEqualTo(recurringMovement.state)
            assertThat(it.autoLaunch).isEqualTo(recurringMovement.autoLaunch)
            assertThat(it.indeterminate).isEqualTo(recurringMovement.indeterminate)
            assertThat(it.totalQuotes).isEqualTo(recurringMovement.totalQuotes)
            assertThat(it.startingQuote).isEqualTo(recurringMovement.startingQuote)
            assertThat(it.currentQuote).isEqualTo(recurringMovement.currentQuote)
            assertThat(it.description).isEqualTo(recurringMovement.description)
            assertThat(it.classification).isEqualTo(recurringMovement.classification)
        })
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/financial/create-recurring-movement.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    fun `should update recurring movement`() {

        val externalId = UUID.fromString("ba870b58-01aa-4477-8ea1-1c644a6770c4")

        val toUpdate = recurringMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        val classificationId = UUID.fromString("ff8ac873-2cbd-43dd-a3e8-2bc451f4e3fa")

        val classification = classificationRepository.findByExternalId(classificationId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Updated"
            this.value = BigDecimal("111.50")
            this.startingAt = LocalDate.of(2025, 1, 1)
            this.state = RecurringMovement.State.ENDED
            this.classification = classification
        }

        recurringMovementService.update(toUpdate)

        val updated = recurringMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(updated).satisfies({
            assertThat(it.id).isEqualTo(toUpdate.id)
            assertThat(it.externalId).isEqualTo(toUpdate.externalId)
            assertThat(it.version).isGreaterThan(toUpdate.version)
            assertThat(it.createdOn).isEqualTo(toUpdate.createdOn)
            assertThat(it.lastUpdate).isAfter(toUpdate.lastUpdate)
            assertThat(it.name).isEqualTo(toUpdate.name)
            assertThat(it.value).isEqualTo(toUpdate.value)
            assertThat(it.startingAt).isEqualTo(toUpdate.startingAt)
            assertThat(it.state).isEqualTo(toUpdate.state)
            assertThat(it.autoLaunch).isEqualTo(toUpdate.autoLaunch)
            assertThat(it.indeterminate).isEqualTo(toUpdate.indeterminate)
            assertThat(it.totalQuotes).isEqualTo(toUpdate.totalQuotes)
            assertThat(it.startingQuote).isEqualTo(toUpdate.startingQuote)
            assertThat(it.currentQuote).isEqualTo(toUpdate.currentQuote)
            assertThat(it.description).isEqualTo(toUpdate.description)
            assertThat(it.classification).isEqualTo(toUpdate.classification)
        })
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/financial/create-recurring-movement.sql"
    )
    fun `should fail to update if movement is ended`() {

        val externalId = UUID.fromString("2afc2759-b38e-4e1a-9a56-3f54c11c7e5c")

        val toUpdate = recurringMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Updated"
        }

        assertThatThrownBy { recurringMovementService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    @Sql(
        "/sql/financial/clear-tables.sql",
        "/sql/registration/clear-tables.sql",
        "/sql/financial/create-recurring-movement.sql"
    )
    fun `should delete period movement`() {

        val externalId = UUID.fromString("2afc2759-b38e-4e1a-9a56-3f54c11c7e5c")

        val toDelete = recurringMovementRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        recurringMovementService.delete(toDelete)

        val deleted = recurringMovementRepository.findByExternalId(externalId)

        assertThat(deleted).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete if movement has launches`() {
        // TODO when launching a new movement into the period, should do this test
    }
}