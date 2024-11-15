package br.com.webbudget.domain.validators.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import org.springframework.stereotype.Component

@Component
@OnCreateValidation
@OnUpdateValidation
class ApportionmentsDistributionValidator : PeriodMovementValidator {

    override fun validate(value: PeriodMovement) {

        val apportionmentsTotal = value.apportionments.sumOf { it.value }

        if (apportionmentsTotal != value.value) {
            throw BusinessException(
                "Apportionments total must be equal to movement value",
                "period-movement.errors.invalid-apportionments"
            )
        }
    }
}