package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
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
        financialPeriodRepository.findByExternalId(value.externalId!!)
            ?.let {
                if (it.cantBeModified()) {
                    throw BusinessException(
                        "You can't delete or update non active periods", "financial-period.errors.period-not-active"
                    )
                }
            }
    }
}
