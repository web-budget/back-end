package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.validators.registration.CostCenterNameValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.fixtures.createCostCenter
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
            .isInstanceOf(ConflictingPropertyException::class.java)

        verify(exactly = 1) { costCenterRepository.findByNameIgnoreCase("Cost Center") }

        confirmVerified(costCenterRepository)
    }

    @Test
    fun `should fail for different entities and equal name with parent`() {

        val expectedName = "Parent > Cost Center"
        val parent = createCostCenter(id = 2L, name = "Parent")

        every {
            costCenterRepository.findByFullNameIgnoreCase(expectedName)
        } returns createCostCenter(parentCostCenter = parent)

        val toValidate = createCostCenter(id = null, externalId = null, parentCostCenter = parent)
        toValidate.updateFullName()

        assertThatThrownBy { costCenterNameValidator.validate(toValidate) }
            .isInstanceOf(ConflictingPropertyException::class.java)

        verify(exactly = 1) { costCenterRepository.findByFullNameIgnoreCase(expectedName) }

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

    @Test
    fun `should not fail if entities are equal with parent`() {

        val expectedName = "Parent > Cost Center"
        val parent = createCostCenter(id = 2L, name = "Parent")

        val externalId = UUID.randomUUID()
        val toValidate = createCostCenter(id = 1L, externalId = externalId, parentCostCenter = parent)
        toValidate.updateFullName()

        every {
            costCenterRepository.findByFullNameIgnoreCaseAndExternalIdNot(expectedName, externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { costCenterNameValidator.validate(toValidate) }

        verify(exactly = 1) {
            costCenterRepository.findByFullNameIgnoreCaseAndExternalIdNot(expectedName, externalId)
        }

        confirmVerified(costCenterRepository)
    }
}
