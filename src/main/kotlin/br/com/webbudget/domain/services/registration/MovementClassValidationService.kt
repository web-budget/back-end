package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.MovementClassValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MovementClassValidationService(
    @OnCreateValidation
    private val creationValidators: List<MovementClassValidator>,
    @OnUpdateValidation
    private val updateValidators: List<MovementClassValidator>
) : ValidationService<MovementClass> {

    override fun validateOnCreate(value: MovementClass) {
        creationValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: MovementClass) {
        updateValidators.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: MovementClass) {
        TODO("Not yet implemented")
    }
}