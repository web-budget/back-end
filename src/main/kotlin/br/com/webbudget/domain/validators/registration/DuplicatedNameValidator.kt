package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.CreationValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Component

@Component
@UpdatingValidation
@CreationValidation
class DuplicatedNameValidator(
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
            ?.let {
                throw DuplicatedPropertyException(
                    "cost-center.name",
                    "cost-center.errors.duplicated-name"
                )
            }
    }

    private fun validateNotSaved(value: CostCenter) {
        costCenterRepository.findByNameIgnoreCase(value.name)
            ?.let {
                throw DuplicatedPropertyException(
                    "cost-center.name",
                    "cost-center.errors.duplicated-name"
                )
            }
    }
}