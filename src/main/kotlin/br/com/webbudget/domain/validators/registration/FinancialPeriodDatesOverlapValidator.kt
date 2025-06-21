package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ErrorCodes.OVERLAPPING_START_END_DATES
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class FinancialPeriodDatesOverlapValidator(
    private val financialPeriodRepository: FinancialPeriodRepository
) : FinancialPeriodValidator {

    override fun validate(value: FinancialPeriod) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    fun validateSaved(value: FinancialPeriod) {

        val periods = financialPeriodRepository.findByStartAndEndDatesAndExternalIdNot(
            value.startingAt,
            value.endingAt,
            value.externalId!!
        )

        if (periods.isNotEmpty()) {
            throw BusinessException(
                "Period start and end dates are overlapping with other open periods",
                OVERLAPPING_START_END_DATES
            )
        }
    }

    fun validateNotSaved(value: FinancialPeriod) {

        val periods = financialPeriodRepository.findByStartAndEndDates(value.startingAt, value.endingAt)

        if (periods.isNotEmpty()) {
            throw BusinessException(
                "Period start and end dates are overlapping with other open periods",
                OVERLAPPING_START_END_DATES
            )
        }
    }
}
