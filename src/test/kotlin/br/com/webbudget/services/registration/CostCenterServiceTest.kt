package br.com.webbudget.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.domain.validators.registration.CostCenterValidator
import br.com.webbudget.domain.validators.registration.DuplicatedNameValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CostCenterServiceTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    private lateinit var costCenterService: CostCenterService

    @BeforeEach
    fun setup() {

        val creatingValidators = listOf<CostCenterValidator>(
            DuplicatedNameValidator(costCenterRepository)
        )

        val updatingValidators = listOf<CostCenterValidator>(
            DuplicatedNameValidator(costCenterRepository)
        )

        this.costCenterService = CostCenterService(costCenterRepository, creatingValidators, updatingValidators)
    }

    @Test
    fun `should pass validation and save`() {

        val toCreate = CostCenter("To create", true)

        every { costCenterRepository.findByNameIgnoreCase("To create") } returns null
        every { costCenterRepository.save(any()) } returns toCreate

        costCenterService.create(toCreate)

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCase("To create") }
        verify(exactly = 1) { costCenterRepository.save(toCreate) }
    }

    @Test
    fun `should fail validation and not save`() {

        val toCreate = CostCenter("To create", true)

        every { costCenterRepository.findByNameIgnoreCase("To create") } returns toCreate

        assertThatThrownBy { costCenterService.create(toCreate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCase("To create") }
        verify { costCenterRepository.save(toCreate) wasNot called }
    }

    @Test
    fun `should pass validation and update`() {

        val externalId = UUID.randomUUID()
        val toUpdate = CostCenter("To update", true)
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every { costCenterRepository.findByNameIgnoreCaseAndExternalIdNot("To update", externalId) } returns null
        every { costCenterRepository.save(any()) } returns toUpdate

        costCenterService.update(toUpdate)

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCaseAndExternalIdNot(any(), any()) }
        verify(exactly = 1) { costCenterRepository.save(toUpdate) }
    }

    @Test
    fun `should fail validation and not update`() {

        val externalId = UUID.randomUUID()
        val toUpdate = CostCenter("To update", true)
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every {
            costCenterRepository.findByNameIgnoreCaseAndExternalIdNot("To update", externalId)
        } returns toUpdate
        every { costCenterRepository.save(any()) } returns toUpdate

        assertThatThrownBy { costCenterService.update(toUpdate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCaseAndExternalIdNot(any(), any()) }
        verify { costCenterRepository.save(toUpdate) wasNot called }
    }

    @Test
    fun `should delete`() {

        val toDelete = CostCenter("To delete", true)

        every { costCenterRepository.delete(any()) } just runs

        costCenterService.delete(toDelete)

        verify(exactly = 1) { costCenterRepository.delete(toDelete) }
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        // TODO do the logic to test constraint violation here
    }
}
