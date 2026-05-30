package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.exceptions.DomainException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class ClassificationNameValidator(
    private val classificationRepository: ClassificationRepository
) : ClassificationValidator {

    override fun validate(value: Classification) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: Classification) {
        classificationRepository.findByNameIgnoreCaseAndExternalIdNot(value.name, value.externalId!!)
            ?.let { throw DomainException.conflict(parameters = mapOf("classification.name" to value.name)) }
    }

    private fun validateNotSaved(value: Classification) {
        classificationRepository.findByNameIgnoreCase(value.name)
            ?.let { throw DomainException.conflict(parameters = mapOf("classification.name" to value.name)) }
    }
}
