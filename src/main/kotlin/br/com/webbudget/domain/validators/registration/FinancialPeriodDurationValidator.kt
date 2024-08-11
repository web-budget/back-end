package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class FinancialPeriodDurationValidator(
    private val financialPeriodRepository: FinancialPeriodRepository
) : FinancialPeriodValidator {

    override fun validate(value: FinancialPeriod) {

        val periods = financialPeriodRepository.findByStartAndEndDates(value.startingAt, value.endingAt)

        if (periods.isNotEmpty()) {
            throw BusinessException(
                "Period start and end dates are conflicting with other open periods",
                "financial-period.errors.invalid-dates"
            )
        }
    }
}
