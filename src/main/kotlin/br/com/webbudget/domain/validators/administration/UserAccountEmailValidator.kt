package br.com.webbudget.domain.validators.administration

import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
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
            ?.let { throw ConflictingPropertyException(parameters = mapOf("user.email" to value.email)) }
    }

    private fun validateNotSaved(value: User) {
        userRepository.findByEmail(value.email)
            ?.let { throw ConflictingPropertyException(parameters = mapOf("user.email" to value.email)) }
    }
}
