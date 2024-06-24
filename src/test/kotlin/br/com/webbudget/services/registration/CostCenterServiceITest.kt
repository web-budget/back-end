package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixture.createCostCenter
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

class CostCenterServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var costCenterService: CostCenterService

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should create`() {

        val toCreate = createCostCenter()
        val externalId = costCenterService.create(toCreate)

        val created = costCenterRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThat(created)
            .satisfies({
                assertThat(it.id).isNotNull()
                assertThat(it.externalId).isEqualTo(externalId)
                assertThat(it.version).isNotNull()
                assertThat(it.createdOn).isNotNull()
                assertThat(it.active).isEqualTo(toCreate.active)
                assertThat(it.name).isEqualTo(toCreate.name)
                assertThat(it.description).isEqualTo(toCreate.description)
            })
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should not create when name is duplicated`() {

        val toCreate = createCostCenter()
        costCenterService.create(toCreate)

        val duplicated = createCostCenter()

        assertThatThrownBy { costCenterService.create(duplicated) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql", "/sql/registration/create-cost-centers.sql")
    fun `should update`() {

        val externalId = UUID.fromString("52e3456b-1b0d-42c5-8be0-07ddaecce441")
        val toUpdate = costCenterRepository.findByExternalId(externalId) ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.name = "Updated"
            this.description = "Updated"
            this.active = false
        }

        val updated = costCenterService.update(toUpdate)

        assertThat(updated)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(toUpdate.id)
                assertThat(it.externalId).isEqualTo(externalId)
                assertThat(it.version).isGreaterThan(toUpdate.version)
                assertThat(it.active).isEqualTo(toUpdate.active)
                assertThat(it.name).isEqualTo(toUpdate.name)
                assertThat(it.description).isEqualTo(toUpdate.description)
            })
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should not update when name is duplicated`() {

        costCenterService.create(createCostCenter(name = "Cost Center One"))
        val externalId = costCenterService.create(createCostCenter(name = "Cost Center Two"))

        val toUpdate = costCenterRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.name = "Cost Center One"
        }

        assertThatThrownBy { costCenterService.update(toUpdate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/registration/clear-tables.sql")
    fun `should delete`() {

        val externalId = costCenterService.create(createCostCenter())

        val toDelete = costCenterRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        costCenterService.delete(toDelete)

        val deleted = costCenterRepository.findByExternalId(externalId)
        assertThat(deleted).isNull()
    }

    @Test
    @Sql(
        "/sql/registration/clear-tables.sql",
        "/sql/registration/create-cost-centers.sql",
        "/sql/registration/create-movement-classes.sql"
    )
    fun `should fail to delete when in use`() {

        val externalId = UUID.fromString("52e3456b-1b0d-42c5-8be0-07ddaecce441")

        val toDelete = costCenterRepository.findByExternalId(externalId) ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThatThrownBy { costCenterService.delete(toDelete) }
            .isInstanceOf(DataIntegrityViolationException::class.java)
    }
}
