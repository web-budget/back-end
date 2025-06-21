package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.entities.registration.MovementClass.Type
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ErrorCodes.BUDGET_LIMIT_EXCEEDED
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@OnUpdateValidation
@OnCreateValidation
class BudgetLimitValidator(
    private val movementClassRepository: MovementClassRepository
) : MovementClassValidator {

    override fun validate(value: MovementClass) {

        // no budget limit, so the movement class is valid
        if (value.budget == null) {
            return
        }

        val costCenter = value.costCenter

        // for income or expense, if cost center doesn't have budget limit, movement class is valid
        if ((value.isForExpense() && costCenter.expenseBudget == null)
            || (value.isForIncome() && costCenter.incomeBudget == null)
        ) {
            return
        }

        // get the cost center budget based on the movement class type
        val costCenterBudget = if (value.isForIncome()) {
            costCenter.incomeBudget!!
        } else {
            costCenter.expenseBudget!!
        }

        if (value.isSaved()) {
            this.validateSaved(value, costCenterBudget)
        } else {
            this.validateNotSaved(value, costCenterBudget)
        }
    }

    private fun validateSaved(value: MovementClass, costCenterBudget: BigDecimal) {

        val currentMovementClass = movementClassRepository.findByExternalId(value.externalId!!)
            ?: error("Can't find movement class with external id [${value.externalId}]")

        val allocatedBudget = movementClassRepository.findBudgetAllocatedByCostCenter(value.costCenter, value.type)

        // before checking, remove the current movement class budget from the total allocated budget
        val remainingBudget = costCenterBudget.subtract(allocatedBudget.total.subtract(currentMovementClass.budget!!))

        check(value.budget!!, remainingBudget, value.type)
    }

    private fun validateNotSaved(value: MovementClass, costCenterBudget: BigDecimal) {

        val allocatedBudget = movementClassRepository.findBudgetAllocatedByCostCenter(value.costCenter, value.type)
        val remainingBudget = costCenterBudget.subtract(allocatedBudget.total)

        check(value.budget!!, remainingBudget, value.type)
    }

    private fun check(budget: BigDecimal, remainingBudget: BigDecimal, type: Type) {
        if (budget > remainingBudget) {
            val parameters = mapOf<String, Any>("available-budget" to remainingBudget, "type" to type)
            throw BusinessException(
                "Only [$remainingBudget] of [$type] budget is available",
                BUDGET_LIMIT_EXCEEDED,
                parameters
            )
        }
    }
}
