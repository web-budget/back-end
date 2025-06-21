package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class MovementClassNameValidator(
    private val movementClassRepository: MovementClassRepository
) : MovementClassValidator {

    override fun validate(value: MovementClass) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: MovementClass) {
        movementClassRepository.findByNameIgnoreCaseAndExternalIdNot(value.name, value.externalId!!)
            ?.let { throw ConflictingPropertyException(parameters = mapOf("movement-class.name" to value.name)) }
    }

    private fun validateNotSaved(value: MovementClass) {
        movementClassRepository.findByNameIgnoreCase(value.name)
            ?.let { throw ConflictingPropertyException(parameters = mapOf("movement-class.name" to value.name)) }
    }
}
