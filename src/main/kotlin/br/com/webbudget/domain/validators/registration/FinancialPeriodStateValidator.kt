package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnUpdateValidation
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
class FinancialPeriodStateValidator : FinancialPeriodValidator {

    override fun validate(value: FinancialPeriod) {
        if (value.isClosed()) {
            throw BusinessException(
                "You can only update open financial periods",
                "financial-period.errors.update-closed-period"
            )
        }
    }
}
