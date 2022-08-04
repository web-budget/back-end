package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CostCenterServiceTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var costCenterService: CostCenterService
    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @BeforeEach
    fun clearDatabase() {
        costCenterRepository.deleteAll()
    }

    @Test
    fun `should create`() {

        val toCreate = CostCenter("To create", true)

        val created = costCenterService.create(toCreate)

        assertThat(created).isEqualTo(toCreate)
    }

    @Test
    fun `should fail if description is duplicated`() {

        val toCreate = CostCenter("Duplicated", true)
        costCenterService.create(toCreate)

        val duplicated = CostCenter("Duplicated", true)

        AssertionsForClassTypes.assertThatThrownBy { costCenterService.create(duplicated) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    fun `should update`() {

        val toCreate = CostCenter("To update", true)
        val created = costCenterService.create(toCreate)

        created.active = false

        val updated = costCenterService.update(created)

        assertThat(updated)
            .isEqualTo(created)
            .hasFieldOrPropertyWithValue("active", false)
    }

    @Test
    fun `should delete`() {

        val toCreate = CostCenter("To delete", true)
        val created = costCenterService.create(toCreate)

        costCenterService.delete(created)

        val found = costCenterRepository.findByExternalId(created.externalId!!)

        assertThat(found).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        // TODO do the logic to test constraint violation here
    }
}
