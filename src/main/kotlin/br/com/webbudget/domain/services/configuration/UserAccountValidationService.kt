package br.com.webbudget.domain.services.configuration

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.CreatingValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.domain.validators.configuration.UserValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserAccountValidationService(
    @CreatingValidation
    private val creatingValidation: List<UserValidator>,
    @UpdatingValidation
    private val updatingValidation: List<UserValidator>
) : ValidationService<User> {

    override fun validateOnCreate(value: User) {
        creatingValidation.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: User) {
        updatingValidation.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: User) {
        TODO("Not yet implemented")
    }
}