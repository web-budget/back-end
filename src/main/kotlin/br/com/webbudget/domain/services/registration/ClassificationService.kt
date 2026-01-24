package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.ClassificationValidator
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ClassificationService(
    private val classificationRepository: ClassificationRepository,
    @OnCreateValidation
    private val creationValidators: List<ClassificationValidator>,
    @OnUpdateValidation
    private val updateValidators: List<ClassificationValidator>
) {

    @Transactional
    fun create(classification: Classification): UUID {

        creationValidators.forEach { it.validate(classification) }

        val created = classificationRepository.persist(classification)
        return created.externalId!!
    }

    @Transactional
    fun update(classification: Classification): Classification {

        updateValidators.forEach { it.validate(classification) }

        return classificationRepository.merge(classification)
    }

    @Transactional
    fun delete(classification: Classification) {
        classificationRepository.delete(classification)
    }
}
