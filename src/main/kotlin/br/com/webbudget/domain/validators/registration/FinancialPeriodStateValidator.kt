package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ACTIVE
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ENDED
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnDeleteValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnDeleteValidation
class FinancialPeriodStateValidator(
    private val financialPeriodRepository: FinancialPeriodRepository
) : FinancialPeriodValidator {

    override fun validate(value: FinancialPeriod) {
        financialPeriodRepository.findByExternalIdAndStatusIn(value.externalId!!, listOf(ACTIVE, ENDED))
            ?: throw BusinessException(
                "You can't delete or update non open periods",
                "financial-period.errors.period-not-open"
            )
    }
}
