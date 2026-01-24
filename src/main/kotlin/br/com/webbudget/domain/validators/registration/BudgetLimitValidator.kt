package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.entities.registration.Classification.Type
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ErrorCodes.BUDGET_LIMIT_EXCEEDED
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@OnUpdateValidation
@OnCreateValidation
class BudgetLimitValidator(
    private val classificationRepository: ClassificationRepository
) : ClassificationValidator {

    override fun validate(value: Classification) {

        // no budget limit, so the movement class is valid
        val costCenter = value.costCenter

        if (value.budget == null || costCenter.isBudgetValidationRequired(value.type).not()) {
            return
        }

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

    private fun validateSaved(value: Classification, costCenterBudget: BigDecimal) {

        val currentMovementClass = classificationRepository.findByExternalId(value.externalId!!)
            ?: error("Can't find movement class with external id [${value.externalId}]")

        val allocatedBudget = classificationRepository.findBudgetAllocatedByCostCenter(value.costCenter, value.type)

        // before checking, remove the current movement class budget from the total allocated budget
        val remainingBudget = costCenterBudget.subtract(allocatedBudget.total.subtract(currentMovementClass.budget!!))

        check(value.budget!!, remainingBudget, value.type)
    }

    private fun validateNotSaved(value: Classification, costCenterBudget: BigDecimal) {

        val allocatedBudget = classificationRepository.findBudgetAllocatedByCostCenter(value.costCenter, value.type)
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
