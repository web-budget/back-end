package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class FinancialPeriodDateConflictValidator : FinancialPeriodValidator {

    override fun validate(value: FinancialPeriod) {

        val startDate = value.startingAt
        val endDate = value.endingAt

        if (startDate.isAfter(endDate)) {
            throw BusinessException("Start date must be before end date", "financial-period.errors.invalid-dates")
        }
    }
}
