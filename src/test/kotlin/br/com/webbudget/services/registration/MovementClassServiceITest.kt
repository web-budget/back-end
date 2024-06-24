package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.MovementClassService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixture.createMovementClass
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.UUID

class MovementClassServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Autowired
    private lateinit var movementClassRepository: MovementClassRepository

    @Autowired
    private lateinit var movementClassService: MovementClassService

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql"
    )
    fun `should create`() {

        val costCenter = costCenterRepository.findByExternalId(UUID.fromString("52e3456b-1b0d-42c5-8be0-07ddaecce441"))
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val toCreate = createMovementClass(costCenter = costCenter, budget = BigDecimal.valueOf(1.99))

        val externalId = movementClassService.create(toCreate)

        val created = movementClassRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

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
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should not create when name is duplicated`() {

        val costCenter = costCenterRepository.findByExternalId(UUID.fromString("52e3456b-1b0d-42c5-8be0-07ddaecce441"))
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        val toCreate = createMovementClass(name = "Mercado", costCenter = costCenter)

        assertThatThrownBy { movementClassService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should update`() {

        val externalId = UUID.fromString("98cb4961-5cde-46fb-abfd-8461be7d628b")
        val toUpdate = movementClassRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Updated"
            this.description = "Updated"
            this.budget = BigDecimal.valueOf(2.99)
            this.active = false
        }

        val updated = movementClassService.update(toUpdate)

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
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should not update when name is duplicated`() {

        val externalId = UUID.fromString("f21d94d2-d28e-4aa3-b12d-8a520023edd9")
        val toUpdate = movementClassRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        toUpdate.apply {
            this.name = "Vendas"
        }

        assertThatThrownBy { movementClassService.update(toUpdate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should delete`() {

        val externalId = UUID.fromString("98cb4961-5cde-46fb-abfd-8461be7d628b")
        val toDelete = movementClassRepository.findByExternalId(externalId) ?: fail { OBJECT_NOT_FOUND_ERROR }

        movementClassService.delete(toDelete)

        val deleted = movementClassRepository.findByExternalId(externalId)
        assertThat(deleted).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        TODO("Not yet implemented")
    }
}