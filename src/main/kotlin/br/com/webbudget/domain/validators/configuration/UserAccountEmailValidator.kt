package br.com.webbudget.domain.validators.configuration

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.CreatingValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.springframework.stereotype.Component

@Component
@UpdatingValidation
@CreatingValidation
class UserAccountEmailValidator(
    private val userRepository: UserRepository
) : UserValidator {

    override fun validate(value: User) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: User) {
        userRepository.findByEmailAndExternalIdNot(value.email, value.externalId!!)
            ?.let { throw DuplicatedPropertyException("user.email", "users.errors.duplicated-email") }
    }

    private fun validateNotSaved(value: User) {
        userRepository.findByEmail(value.email)
            ?.let { throw DuplicatedPropertyException("user.email", "users.errors.duplicated-email") }
    }
}
