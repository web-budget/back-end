package br.com.webbudget.validators.configuration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.configuration.DuplicatedEmailValidator
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class UserValidatorsTest : BaseIntegrationTest() {

    @MockK
    lateinit var userRepository: UserRepository

    lateinit var duplicatedEmailValidator: DuplicatedEmailValidator

    @BeforeEach
    fun setup() {
        this.duplicatedEmailValidator = DuplicatedEmailValidator(userRepository)
    }

    @Test
    fun `should fail when duplicated email`() {

        val duplicated = User("Duplicated", "test@test.com", "123", false, listOf())
            .apply {
                this.id = 1L
                this.externalId = UUID.randomUUID()
            }

        every { userRepository.findByEmail("test@test.com") } returns duplicated

        val toValidate = User("Duplicated", "test@test.com", "123", false, listOf())

        assertThatThrownBy { duplicatedEmailValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("user.email")
    }

    @Test
    fun `should pass when same entity is checked for duplicated email`() {

        val externalId = UUID.randomUUID()

        val notDuplicated = User("Not Duplicated", "test@test.com", "123", false, listOf())
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        every {
            userRepository.findByEmailAndExternalIdNot("test@test.com", externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { duplicatedEmailValidator.validate(notDuplicated) }
    }
}
