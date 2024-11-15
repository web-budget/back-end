package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.MovementClassValidator
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class MovementClassService(
    private val movementClassRepository: MovementClassRepository,
    @OnCreateValidation
    private val creationValidators: List<MovementClassValidator>,
    @OnUpdateValidation
    private val updateValidators: List<MovementClassValidator>
) {

    @Transactional
    fun create(movementClass: MovementClass): UUID {

        creationValidators.forEach { it.validate(movementClass) }

        val created = movementClassRepository.persist(movementClass)
        return created.externalId!!
    }

    @Transactional
    fun update(movementClass: MovementClass): MovementClass {

        updateValidators.forEach { it.validate(movementClass) }

        return movementClassRepository.merge(movementClass)
    }

    @Transactional
    fun delete(movementClass: MovementClass) {
        movementClassRepository.delete(movementClass)
    }
}
