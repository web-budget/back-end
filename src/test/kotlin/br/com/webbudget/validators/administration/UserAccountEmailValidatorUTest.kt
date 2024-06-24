package br.com.webbudget.validators.administration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.administration.UserAccountEmailValidator
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.createUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class UserAccountEmailValidatorUTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var userAccountEmailValidator: UserAccountEmailValidator

    @Test
    fun `should fail for different entities and equal email`() {

        every { userRepository.findByEmail("user@test.com") } returns createUser()

        val toValidate = createUser(id = null, externalId = null)

        assertThatThrownBy { userAccountEmailValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("users.errors.duplicated-email")

        verify(exactly = 1) { userRepository.findByEmail("user@test.com") }

        confirmVerified(userRepository)
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()
        val toValidate = createUser(id = 1L, externalId = externalId)

        every {
            userRepository.findByEmailAndExternalIdNot("user@test.com", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { userAccountEmailValidator.validate(toValidate) }

        verify(exactly = 1) { userRepository.findByEmailAndExternalIdNot("user@test.com", externalId) }

        confirmVerified(userRepository)
    }
}
