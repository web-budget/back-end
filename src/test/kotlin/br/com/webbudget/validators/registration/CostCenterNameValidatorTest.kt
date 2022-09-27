package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.CostCenterNameValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CostCenterNameValidatorTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    @InjectMockKs
    private lateinit var costCenterNameValidator: CostCenterNameValidator

    @Test
    fun `should fail for different entities and equal name`() {

        val duplicated = CostCenter("Duplicated", true)
            .apply {
                this.id = 1L
                this.externalId = UUID.randomUUID()
            }

        every {
            costCenterRepository.findByNameIgnoreCase("Duplicated")
        } returns duplicated

        val toValidate = CostCenter("Duplicated", true)

        assertThatThrownBy { costCenterNameValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("cost-center.name")

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCase("Duplicated") }
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()

        val notDuplicated = CostCenter("Not duplicated", true)
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every {
            costCenterRepository.findByNameIgnoreCaseAndExternalIdNot("Not duplicated", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { costCenterNameValidator.validate(notDuplicated) }

        verify(exactly = 1) {
            costCenterRepository.findByNameIgnoreCaseAndExternalIdNot("Not duplicated", externalId)
        }
    }
}
