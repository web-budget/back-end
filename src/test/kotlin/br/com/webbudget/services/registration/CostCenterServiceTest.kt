package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.domain.services.registration.CostCenterValidationService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixture.CostCenterFixture
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class CostCenterServiceTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var costCenterValidationService: CostCenterValidationService

    @Autowired
    private lateinit var costCenterService: CostCenterService

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Test
    fun `should save when validation pass`() {

        val toCreate = CostCenterFixture.create()

        every { costCenterValidationService.validateOnCreate(any()) } just runs

        val externalId = costCenterService.create(toCreate)
        val created = costCenterRepository.findByExternalId(externalId)

        assertThat(created)
            .isNotNull
            .hasFieldOrProperty("id").isNotNull
            .hasFieldOrProperty("externalId").isNotNull
            .hasFieldOrProperty("createdOn").isNotNull
            .hasFieldOrProperty("version").isNotNull
            .hasFieldOrPropertyWithValue("active", toCreate.active)
            .hasFieldOrPropertyWithValue("name", toCreate.name)
            .hasFieldOrPropertyWithValue("description", toCreate.description)
    }

    @Test
    fun `should not save when validation fail`() {

        val toCreate = CostCenterFixture.create()

        every { costCenterValidationService.validateOnCreate(any()) } throws
                RuntimeException("Ops, something is wrong!")

        assertThatThrownBy { costCenterService.create(toCreate) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should update when validation pass`() {

        every { costCenterValidationService.validateOnCreate(any()) } just runs
        every { costCenterValidationService.validateOnUpdate(any()) } just runs

        val toCreate = CostCenterFixture.create()
        val externalId = costCenterService.create(toCreate)
        val toUpdate = costCenterRepository.findByExternalId(externalId)

        assertThat(toUpdate).isNotNull

        toUpdate!!.apply {
            this.name = "Updated"
            this.description = "New description"
            this.active = false
        }

        val updated = costCenterService.update(toUpdate)

        assertThat(updated)
            .isNotNull
            .hasFieldOrPropertyWithValue("id", toUpdate.id)
            .hasFieldOrPropertyWithValue("externalId", toUpdate.externalId)
            .hasFieldOrPropertyWithValue("createdOn", toUpdate.createdOn)
            .hasFieldOrPropertyWithValue("active", toUpdate.active)
            .hasFieldOrPropertyWithValue("name", toUpdate.name)
            .hasFieldOrPropertyWithValue("description", toUpdate.description)
            .extracting {
                assertThat(it.version)
                    .isGreaterThan(toUpdate.version)
            }
    }

    @Test
    fun `should not update when validation fail`() {

        val toUpdate = CostCenterFixture.create(1L, UUID.randomUUID())

        every { costCenterValidationService.validateOnCreate(any()) } throws
                RuntimeException("Ops, something is wrong!")

        assertThatThrownBy { costCenterService.update(toUpdate) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should delete`() {

        every { costCenterValidationService.validateOnCreate(any()) } just runs
        every { costCenterValidationService.validateOnDelete(any()) } just runs

        val toCreate = CostCenterFixture.create()
        val externalId = costCenterService.create(toCreate)
        val toDelete = costCenterRepository.findByExternalId(externalId)!!

        assertThat(toDelete).isNotNull

        costCenterService.delete(toDelete)

        val deleted = costCenterRepository.findByExternalId(externalId)

        assertThat(deleted).isNull()
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        // TODO do the logic to test constraint violation here
    }
}
