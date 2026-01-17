package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.FinancialPeriodCreateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodListView
import br.com.webbudget.application.payloads.registration.FinancialPeriodUpdateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodView
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import org.springframework.stereotype.Component

@Component
class FinancialPeriodMapper {

    fun mapToView(financialPeriod: FinancialPeriod): FinancialPeriodView = FinancialPeriodView(
        id = financialPeriod.externalId!!,
        name = financialPeriod.name,
        startingAt = financialPeriod.startingAt,
        endingAt = financialPeriod.endingAt,
        status = financialPeriod.status.name,
        expensesGoal = financialPeriod.expensesGoal,
        revenuesGoal = financialPeriod.revenuesGoal
    )

    fun mapToListView(financialPeriod: FinancialPeriod): FinancialPeriodListView = FinancialPeriodListView(
        id = financialPeriod.externalId!!,
        name = financialPeriod.name,
        startingAt = financialPeriod.startingAt,
        endingAt = financialPeriod.endingAt,
        status = financialPeriod.status.name,
    )

    fun mapToDomain(form: FinancialPeriodCreateForm): FinancialPeriod = FinancialPeriod(
        name = form.name!!,
        startingAt = form.startingAt!!,
        endingAt = form.endingAt!!,
        expensesGoal = form.expensesGoal,
        revenuesGoal = form.revenuesGoal
    )

    fun mapToDomain(form: FinancialPeriodUpdateForm, financialPeriod: FinancialPeriod) = financialPeriod.apply {
        this.name = form.name!!
        this.expensesGoal = form.expensesGoal
        this.revenuesGoal = form.revenuesGoal
    }
}