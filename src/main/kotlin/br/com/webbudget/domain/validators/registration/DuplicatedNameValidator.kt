package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.BusinessValidator.Companion.ON_CREATE
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Qualifier(ON_CREATE)
class DuplicatedNameValidator(
    private val costCenterRepository: CostCenterRepository
) : CostCenterValidator {

    override fun validate(value: CostCenter) {
        costCenterRepository.findByDescriptionIgnoreCase(value.description)
            ?.let {
                throw DuplicatedPropertyException(
                    "cost-center.description",
                    "cost-center.errors.duplicated-description"
                )
            }
    }
}