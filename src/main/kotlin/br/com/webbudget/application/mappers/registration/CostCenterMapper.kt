package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.CostCenterCreateForm
import br.com.webbudget.application.payloads.registration.CostCenterListView
import br.com.webbudget.application.payloads.registration.CostCenterUpdateForm
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.entities.registration.CostCenter
import org.springframework.stereotype.Component

@Component
class CostCenterMapper {

    fun mapToView(costCenter: CostCenter): CostCenterView = CostCenterView(
        id = costCenter.externalId!!,
        name = costCenter.name,
        active = costCenter.active,
        description = costCenter.description,
        incomeBudget = costCenter.incomeBudget,
        expenseBudget = costCenter.expenseBudget
    )

    fun mapToListView(costCenter: CostCenter): CostCenterListView = CostCenterListView(
        id = costCenter.externalId!!,
        name = costCenter.name,
        active = costCenter.active,
        incomeBudget = costCenter.incomeBudget,
        expenseBudget = costCenter.expenseBudget
    )

    fun mapToDomain(form: CostCenterCreateForm): CostCenter = CostCenter(
        name = form.name!!,
        description = form.description,
        incomeBudget = form.incomeBudget,
        expenseBudget = form.expenseBudget
    )

    fun mapToDomain(form: CostCenterUpdateForm, costCenter: CostCenter) = costCenter.apply {
        this.name = form.name!!
        this.active = form.active!!
        this.description = form.description
        this.incomeBudget = form.incomeBudget
        this.expenseBudget = form.expenseBudget
    }
}
