package br.com.webbudget.domain.validators.user

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.springframework.stereotype.Component

@Component
class DuplicatedEmailValidator(
    private val userRepository: UserRepository
) : UserCreationValidator {

    override fun validate(value: User) {
        userRepository.findByEmail(value.email)
            ?.let { throw BusinessException("users.error.duplicated-email", "The e-mail address is already in use") }
    }
}
