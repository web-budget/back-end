package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class CostCenterNameValidator(
    private val costCenterRepository: CostCenterRepository
) : CostCenterValidator {

    override fun validate(value: CostCenter) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: CostCenter) {
        costCenterRepository.findByNameIgnoreCaseAndExternalIdNot(value.name, value.externalId!!)
            ?.let { throw ConflictingPropertyException(mapOf("cost-center.name" to value.name)) }
    }

    private fun validateNotSaved(value: CostCenter) {
        costCenterRepository.findByNameIgnoreCase(value.name)
            ?.let { throw ConflictingPropertyException(mapOf("cost-center.name" to value.name)) }
    }
}
