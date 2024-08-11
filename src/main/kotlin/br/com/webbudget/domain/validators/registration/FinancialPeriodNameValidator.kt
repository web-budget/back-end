package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class FinancialPeriodNameValidator(
    private val financialPeriodRepository: FinancialPeriodRepository
) : FinancialPeriodValidator {

    override fun validate(value: FinancialPeriod) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: FinancialPeriod) {
        financialPeriodRepository.findByNameIgnoreCaseAndExternalIdNot(value.name, value.externalId!!)
            ?.let {
                throw DuplicatedPropertyException(
                    "financial-period.errors.duplicated-name",
                    "financial-period.name"
                )
            }
    }

    private fun validateNotSaved(value: FinancialPeriod) {
        financialPeriodRepository.findByNameIgnoreCase(value.name)
            ?.let {
                throw DuplicatedPropertyException(
                    "financial-period.errors.duplicated-name",
                    "financial-period.name"
                )
            }
    }
}
