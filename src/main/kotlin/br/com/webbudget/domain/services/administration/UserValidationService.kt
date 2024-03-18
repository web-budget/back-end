package br.com.webbudget.domain.services.administration

import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.administration.UserValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserValidationService(
    @OnCreateValidation
    private val creationValidators: List<UserValidator>,
    @OnUpdateValidation
    private val updateValidators: List<UserValidator>
) : ValidationService<User> {

    override fun validateOnCreate(value: User) {
        creationValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: User) {
        updateValidators.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: User) {
        TODO("Not yet implemented")
    }
}
