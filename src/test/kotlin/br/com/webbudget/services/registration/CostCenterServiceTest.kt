package br.com.webbudget.services.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.application.payloads.registration.CostCenterForm
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.domain.services.registration.CostCenterValidationService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixture.CostCenterFixture.create
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
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

        val toCreate = create()

        every { costCenterValidationService.validateOnCreate(any()) } just runs

        val externalId = costCenterService.create(toCreate)

        val created = costCenterRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThat(created)
            .satisfies({
                assertThat(it.id).isNotNull()
                assertThat(it.externalId).isNotNull()
                assertThat(it.version).isNotNull()
                assertThat(it.createdOn).isNotNull()
                assertThat(it.active).isEqualTo(toCreate.active)
                assertThat(it.name).isEqualTo(toCreate.name)
                assertThat(it.description).isEqualTo(toCreate.description)
            })
    }

    @Test
    fun `should not save when validation fail`() {

        val toCreate = create()

        every { costCenterValidationService.validateOnCreate(any()) } throws
                RuntimeException("Oops, something went wrong!")

        assertThatThrownBy { costCenterService.create(toCreate) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should update when validation pass`() {

        every { costCenterValidationService.validateOnCreate(any()) } just runs
        every { costCenterValidationService.validateOnUpdate(any()) } just runs

        val toCreate = create()
        val form = CostCenterForm(false, "Updated", "Updated")

        val externalId = costCenterService.create(toCreate)
        val toUpdate = costCenterRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.updateFields(form)
        val updated = costCenterService.update(toUpdate)

        assertThat(updated)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(toUpdate.id)
                assertThat(it.externalId).isEqualTo(toUpdate.externalId)
                assertThat(it.version).isGreaterThan(toUpdate.version)
                assertThat(it.createdOn).isEqualTo(toUpdate.createdOn)
                assertThat(it.active).isEqualTo(toUpdate.active)
                assertThat(it.name).isEqualTo(toUpdate.name)
                assertThat(it.description).isEqualTo(toUpdate.description)
            })
    }

    @Test
    fun `should not update when validation fail`() {

        val toUpdate = create(1L, UUID.randomUUID())

        every { costCenterValidationService.validateOnCreate(any()) } throws
                RuntimeException("Oops, something went wrong!")

        assertThatThrownBy { costCenterService.update(toUpdate) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `should delete`() {

        every { costCenterValidationService.validateOnCreate(any()) } just runs
        every { costCenterValidationService.validateOnDelete(any()) } just runs

        val toCreate = create()
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
