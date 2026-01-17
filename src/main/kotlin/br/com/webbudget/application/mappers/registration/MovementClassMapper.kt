package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.MovementClassCreateForm
import br.com.webbudget.application.payloads.registration.MovementClassListView
import br.com.webbudget.application.payloads.registration.MovementClassUpdateForm
import br.com.webbudget.application.payloads.registration.MovementClassView
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MovementClassMapper(
    private val costCenterMapper: CostCenterMapper,
    private val costCenterRepository: CostCenterRepository
) {

    fun mapToView(movementClass: MovementClass): MovementClassView = MovementClassView(
        id = movementClass.externalId!!,
        name = movementClass.name,
        type = movementClass.type.name,
        active = movementClass.active,
        costCenter = costCenterMapper.mapToListView(movementClass.costCenter),
        budget = movementClass.budget,
        description = movementClass.description
    )

    fun mapToListView(movementClass: MovementClass): MovementClassListView = MovementClassListView(
        id = movementClass.externalId!!,
        name = movementClass.name,
        type = movementClass.type.name,
        active = movementClass.active,
        costCenter = costCenterMapper.mapToListView(movementClass.costCenter),
    )

    fun mapToDomain(form: MovementClassCreateForm): MovementClass = MovementClass(
        name = form.name!!,
        type = form.type!!,
        costCenter = mapCostCenter(form.costCenter!!),
        budget = form.budget,
        description = form.description
    )

    fun mapToDomain(form: MovementClassUpdateForm, movementClass: MovementClass) = movementClass.apply {
        this.name = form.name!!
        this.costCenter = mapCostCenter(form.costCenter!!)
        this.description = form.description
        this.budget = form.budget
        this.active = form.active!!
    }

    private fun mapCostCenter(externalId: UUID): CostCenter =
        costCenterRepository.findByExternalId(externalId) ?: throw ResourceNotFoundException()
}