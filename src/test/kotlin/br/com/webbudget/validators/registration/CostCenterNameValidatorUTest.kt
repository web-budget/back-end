package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.CostCenterNameValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixture.createCostCenter
import io.mockk.confirmVerified
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
class CostCenterNameValidatorUTest {

    @MockK
    private lateinit var costCenterRepository: CostCenterRepository

    @InjectMockKs
    private lateinit var costCenterNameValidator: CostCenterNameValidator

    @Test
    fun `should fail for different entities and equal name`() {

        every {
            costCenterRepository.findByNameIgnoreCase("Cost Center")
        } returns createCostCenter()

        val toValidate = createCostCenter(id = null, externalId = null)

        assertThatThrownBy { costCenterNameValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("cost-center.errors.duplicated-name")

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCase("Cost Center") }

        confirmVerified(costCenterRepository)
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()
        val toValidate = createCostCenter(id = 1L, externalId = externalId)

        every {
            costCenterRepository.findByNameIgnoreCaseAndExternalIdNot("Cost Center", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { costCenterNameValidator.validate(toValidate) }

        verify(exactly = 1) {
            costCenterRepository.findByNameIgnoreCaseAndExternalIdNot("Cost Center", externalId)
        }

        confirmVerified(costCenterRepository)
    }
}
