package br.com.webbudget.validators.administration

import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.administration.UserAccountEmailValidator
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.UserFixture.create
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class UserAccountEmailValidatorTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var userAccountEmailValidator: UserAccountEmailValidator

    @Test
    fun `should fail when duplicated email`() {

        val duplicated = create()
            .apply {
                this.id = 1L
                this.externalId = UUID.randomUUID()
            }

        every { userRepository.findByEmail("user@test.com") } returns duplicated

        val toValidate = create()

        assertThatThrownBy { userAccountEmailValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("users.errors.duplicated-email")
    }

    @Test
    fun `should pass when same entity is checked for duplicated email`() {

        val externalId = UUID.randomUUID()

        val notDuplicated = create()
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every {
            userRepository.findByEmailAndExternalIdNot("user@test.com", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { userAccountEmailValidator.validate(notDuplicated) }
    }
}
