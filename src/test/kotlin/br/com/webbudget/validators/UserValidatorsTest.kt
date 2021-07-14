package br.com.webbudget.validators

import br.com.webbudget.AbstractTest
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.user.DuplicatedEmailValidator
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class UserValidatorsTest : AbstractTest() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var duplicatedEmailValidator: DuplicatedEmailValidator

    @Test
    fun `should fail when duplicated email`() {

        val user = User("Checking", "checking@teste.com", "123", false, listOf())
        userRepository.save(user)

        val other = User("Checking", "checking@teste.com", "123", false, listOf())
        assertThrows<BusinessException>("users.error.duplicated-email") {
            duplicatedEmailValidator.validate(other)
        }
    }

    @Test
    fun `should pass when same entity is checked for duplicated email`() {
        val saved = userRepository.save(User("Duplicated", "duplicated@teste.com", "123", false, listOf()))
        assertDoesNotThrow { duplicatedEmailValidator.validate(saved) }
    }
}
