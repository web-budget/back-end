package br.com.webbudget.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.domain.services.registration.CostCenterValidationService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixture.CostCenterFixture
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CostCenterServiceTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    @MockK
    private lateinit var costCenterValidationService: CostCenterValidationService

    @InjectMockKs
    private lateinit var costCenterService: CostCenterService

    @Test
    fun `should save when validation pass`() {

        val toCreate = CostCenterFixture.create(1L, UUID.randomUUID())

        every { costCenterValidationService.validateOnCreate(any()) } just runs
        every { costCenterRepository.save(any()) } returns toCreate

        costCenterService.create(toCreate)

        verify(exactly = 1) { costCenterValidationService.validateOnCreate(any()) }
        verify(exactly = 1) { costCenterRepository.save(toCreate) }
    }

    @Test
    fun `should not save when validation fail`() {

        val toCreate = CostCenterFixture.create(1L, UUID.randomUUID())

        every { costCenterValidationService.validateOnCreate(any()) } throws RuntimeException("Validation fail")

        assertThatThrownBy { costCenterService.create(toCreate) }
            .isInstanceOf(RuntimeException::class.java)

        verify(exactly = 1) { costCenterValidationService.validateOnCreate(any()) }
        verify { costCenterRepository.save(toCreate) wasNot called }
    }

    @Test
    fun `should update when validation pass`() {

        val toUpdate = CostCenterFixture.create(1L, UUID.randomUUID())

        every { costCenterValidationService.validateOnUpdate(any()) } just runs
        every { costCenterRepository.save(any()) } returns toUpdate

        costCenterService.update(toUpdate)

        verify(exactly = 1) { costCenterValidationService.validateOnUpdate(any()) }
        verify(exactly = 1) { costCenterRepository.save(toUpdate) }
    }

    @Test
    fun `should not update when validation fail`() {

        val toUpdate = CostCenterFixture.create(1L, UUID.randomUUID())

        every { costCenterValidationService.validateOnUpdate(any()) } throws RuntimeException("Validation fail")

        assertThatThrownBy { costCenterService.update(toUpdate) }
            .isInstanceOf(RuntimeException::class.java)

        verify(exactly = 1) { costCenterValidationService.validateOnUpdate(any()) }
        verify { costCenterRepository.save(toUpdate) wasNot called }
    }

    @Test
    fun `should delete`() {

        val toDelete = CostCenterFixture.create(1L, UUID.randomUUID())

        every { costCenterRepository.delete(any<CostCenter>()) } just runs

        costCenterService.delete(toDelete)

        verify(exactly = 1) { costCenterRepository.delete(toDelete) }
    }

    @Test
    @Disabled
    fun `should fail to delete when in use`() {
        // TODO do the logic to test constraint violation here
    }
}
