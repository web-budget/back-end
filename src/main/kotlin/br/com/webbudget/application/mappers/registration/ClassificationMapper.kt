package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.ClassificationCreateForm
import br.com.webbudget.application.payloads.registration.ClassificationListView
import br.com.webbudget.application.payloads.registration.ClassificationUpdateForm
import br.com.webbudget.application.payloads.registration.ClassificationView
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ClassificationMapper(
    private val costCenterMapper: CostCenterMapper,
    private val costCenterRepository: CostCenterRepository
) {

    fun mapToView(classification: Classification): ClassificationView = ClassificationView(
        id = classification.externalId!!,
        name = classification.name,
        type = classification.type.name,
        active = classification.active,
        costCenter = costCenterMapper.mapToListView(classification.costCenter),
        budget = classification.budget,
        description = classification.description
    )

    fun mapToListView(classification: Classification): ClassificationListView = ClassificationListView(
        id = classification.externalId!!,
        name = classification.name,
        type = classification.type.name,
        active = classification.active,
        costCenter = costCenterMapper.mapToListView(classification.costCenter),
    )

    fun mapToDomain(form: ClassificationCreateForm): Classification = Classification(
        name = form.name!!,
        type = form.type!!,
        costCenter = mapCostCenter(form.costCenter!!),
        budget = form.budget,
        description = form.description
    )

    fun mapToDomain(form: ClassificationUpdateForm, classification: Classification) = classification.apply {
        this.name = form.name!!
        this.costCenter = mapCostCenter(form.costCenter!!)
        this.description = form.description
        this.budget = form.budget
        this.active = form.active!!
    }

    private fun mapCostCenter(externalId: UUID): CostCenter =
        costCenterRepository.findByExternalId(externalId) ?: throw ResourceNotFoundException()
}