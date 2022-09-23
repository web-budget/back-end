package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.DuplicatedNameValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CostCenterValidatorsTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    private lateinit var duplicatedNameValidator: DuplicatedNameValidator

    @BeforeEach
    fun setup() {
        duplicatedNameValidator = DuplicatedNameValidator(costCenterRepository)
    }

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

        assertThatThrownBy { duplicatedNameValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("cost-center.name")
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
            .isThrownBy { duplicatedNameValidator.validate(notDuplicated) }
    }
}
