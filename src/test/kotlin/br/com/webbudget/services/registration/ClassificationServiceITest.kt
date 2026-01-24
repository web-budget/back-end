package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.services.registration.ClassificationService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.utilities.fixtures.createClassification
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.UUID
import java.util.stream.Stream

class ClassificationServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Autowired
    private lateinit var classificationRepository: ClassificationRepository

    @Autowired
    private lateinit var classificationService: ClassificationService

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql"
    )
    fun `should create`() {

        val costCenter = costCenterRepository.findByExternalId(UUID.fromString("52e3456b-1b0d-42c5-8be0-07ddaecce441"))
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val toCreate = createClassification(costCenter = costCenter, budget = BigDecimal.valueOf(1.99))

        val externalId = classificationService.create(toCreate)

        val created = classificationRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(created)
            .isNotNull
            .satisfies({
                assertThat(it.id).isNotNull
                assertThat(it.externalId).isEqualTo(externalId)
                assertThat(it.version).isNotNull()
                assertThat(it.name).isEqualTo(toCreate.name)
                assertThat(it.type).isEqualTo(toCreate.type)
                assertThat(it.active).isEqualTo(toCreate.active)
                assertThat(it.budget).isEqualTo(toCreate.budget)
                assertThat(it.description).isEqualTo(toCreate.description)
            })
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    fun `should not create when name is duplicated`() {

        val costCenter = costCenterRepository.findByExternalId(UUID.fromString("52e3456b-1b0d-42c5-8be0-07ddaecce441"))
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val toCreate = createClassification(name = "Mercado", costCenter = costCenter, budget = null)

        assertThatThrownBy { classificationService.create(toCreate) }
            .isInstanceOf(ConflictingPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    fun `should update`() {

        val externalId = UUID.fromString("98cb4961-5cde-46fb-abfd-8461be7d628b")
        val toUpdate = classificationRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Updated"
            this.description = "Updated"
            this.budget = BigDecimal.valueOf(2.99)
            this.active = false
        }

        val updated = classificationService.update(toUpdate)

        assertThat(updated)
            .isNotNull
            .satisfies({
                assertThat(it.externalId).isEqualTo(externalId)
                assertThat(it.version).isEqualTo(1)
                assertThat(it.name).isEqualTo(toUpdate.name)
                assertThat(it.type).isEqualTo(toUpdate.type)
                assertThat(it.active).isEqualTo(toUpdate.active)
                assertThat(it.budget).isEqualTo(toUpdate.budget)
                assertThat(it.description).isEqualTo(toUpdate.description)
            })
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    fun `should not update when name is duplicated`() {

        val externalId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")

        val toUpdate = classificationRepository.findByExternalId(externalId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Vendas"
            this.budget = null
        }

        assertThatThrownBy { classificationService.update(toUpdate) }
            .isInstanceOf(ConflictingPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    fun `should delete`() {

        val externalId = UUID.fromString("98cb4961-5cde-46fb-abfd-8461be7d628b")

        val toDelete = classificationRepository.findByExternalId(externalId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        classificationService.delete(toDelete)

        val deleted = classificationRepository.findByExternalId(externalId)
        assertThat(deleted).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        // TODO this should be done after the movement feature is created
    }

    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql"
    )
    @ParameterizedTest
    @MethodSource("movementClassesToCreate")
    fun `should validate cost center budget limit on create`(toCreate: Classification) {

        val costCenter = costCenterRepository.findByExternalId(UUID.fromString("3cb5732d-2551-4eb9-8b41-f5d312ba7aac"))
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        toCreate.costCenter = costCenter

        assertThatThrownBy { classificationService.create(toCreate) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-classifications.sql"
    )
    @ParameterizedTest
    @MethodSource("movementClassesToUpdate")
    fun `should validate cost center budget limit on update`(externalId: UUID) {

        val toUpdate = classificationRepository.findByExternalId(externalId)
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.budget = BigDecimal.valueOf(1001)
        }

        assertThatThrownBy { classificationService.update(toUpdate) }
            .isInstanceOf(BusinessException::class.java)
    }

    companion object {

        @JvmStatic
        fun movementClassesToCreate(): Stream<Arguments> = Stream.of(
            Arguments.of(
                createClassification(
                    name = "Impostos",
                    type = Classification.Type.EXPENSE,
                    budget = BigDecimal.valueOf(1001),
                )
            ),
            Arguments.of(
                createClassification(
                    name = "Trabalho como uber",
                    type = Classification.Type.INCOME,
                    budget = BigDecimal.valueOf(1001),
                )
            )
        )

        @JvmStatic
        fun movementClassesToUpdate(): Stream<Arguments> = Stream.of(
            Arguments.of(UUID.fromString("86158792-f34e-4cdf-bce6-44394d645d0d")),
            Arguments.of(UUID.fromString("067e62d5-725f-44c6-bfdf-79f9cf19fff8"))
        )
    }
}