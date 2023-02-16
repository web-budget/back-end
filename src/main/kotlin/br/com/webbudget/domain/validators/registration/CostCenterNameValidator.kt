package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.CreatingValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Component

@Component
@UpdatingValidation
@CreatingValidation
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
            ?.let { throw DuplicatedPropertyException("cost-center.errors.duplicated-name", "cost-center.name") }
    }

    private fun validateNotSaved(value: CostCenter) {
        costCenterRepository.findByNameIgnoreCase(value.name)
            ?.let { throw DuplicatedPropertyException("cost-center.errors.duplicated-name", "cost-center.name") }
    }
}
