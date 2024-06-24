package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.MovementClassNameValidator
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixture.createMovementClass
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
class MovementClassNameValidatorUTest {

    @MockK
    private lateinit var movementClassRepository: MovementClassRepository

    @InjectMockKs
    private lateinit var movementClassNameValidator: MovementClassNameValidator

    @Test
    fun `should fail for different entities and equal name`() {

        every { movementClassRepository.findByNameIgnoreCase(eq("Movement Class")) } returns createMovementClass()

        val toValidate = createMovementClass(id = null, externalId = null)

        assertThatThrownBy { movementClassNameValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("movement-class.errors.duplicated-name")

        verify(exactly = 1) { movementClassRepository.findByNameIgnoreCase(eq("Movement Class")) }

        confirmVerified(movementClassRepository)
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()
        val toValidate = createMovementClass(id = 1L, externalId = externalId)

        every {
            movementClassRepository.findByNameIgnoreCaseAndExternalIdNot(eq("Movement Class"), externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { movementClassNameValidator.validate(toValidate) }

        verify(exactly = 1) {
            movementClassRepository.findByNameIgnoreCaseAndExternalIdNot(eq("Movement Class"), externalId)
        }

        confirmVerified(movementClassRepository)
    }
}