package br.com.webbudget.domain.validators.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.ensure
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
class PeriodMovementStateValidator : PeriodMovementValidator {

    override fun validate(value: PeriodMovement) {
        ensure(value.isAccounted().not()) {
            BusinessException("Period movement is not open", "period-movement.errors.not-open")
        }
    }
}