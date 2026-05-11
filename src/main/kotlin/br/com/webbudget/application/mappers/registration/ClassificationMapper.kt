package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.ClassificationCreateForm
import br.com.webbudget.application.payloads.registration.ClassificationListView
import br.com.webbudget.application.payloads.registration.ClassificationUpdateForm
import br.com.webbudget.application.payloads.registration.ClassificationView
import br.com.webbudget.domain.entities.registration.Classification
import org.springframework.stereotype.Component

@Component
class ClassificationMapper {

    fun mapToView(classification: Classification): ClassificationView = ClassificationView(
        id = classification.externalId!!,
        name = classification.name,
        type = classification.type.name,
        active = classification.active,
        budget = classification.budget,
        description = classification.description
    )

    fun mapToListView(classification: Classification): ClassificationListView = ClassificationListView(
        id = classification.externalId!!,
        name = classification.name,
        type = classification.type.name,
        active = classification.active,
    )

    fun mapToDomain(form: ClassificationCreateForm): Classification = Classification(
        name = form.name!!,
        type = form.type!!,
        budget = form.budget,
        description = form.description
    )

    fun mapToDomain(form: ClassificationUpdateForm, classification: Classification) = classification.apply {
        this.name = form.name!!
        this.description = form.description
        this.budget = form.budget
        this.active = form.active!!
    }
}
