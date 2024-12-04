package br.com.webbudget.domain.validators.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.utilities.ensure
import org.springframework.stereotype.Component

@Component
@OnCreateValidation
@OnUpdateValidation
class PeriodActiveValidator : PeriodMovementValidator {

    override fun validate(value: PeriodMovement) {
        ensure(value.financialPeriod.isOpen()) {
            BusinessException("Financial period is not open", "period-movement.errors.period-not-open")
        }
    }
}