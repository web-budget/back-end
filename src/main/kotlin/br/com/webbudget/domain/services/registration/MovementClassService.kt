package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class MovementClassService(
    private val movementClassRepository: MovementClassRepository,
    private val movementClassValidationService: MovementClassValidationService
) {

    @Transactional
    fun create(movementClass: MovementClass): UUID {
        movementClassValidationService.validateOnCreate(movementClass)
        val created = movementClassRepository.persist(movementClass)
        return created.externalId!!
    }

    @Transactional
    fun update(movementClass: MovementClass): MovementClass {
        movementClassValidationService.validateOnUpdate(movementClass)
        return movementClassRepository.merge(movementClass)
    }

    @Transactional
    fun delete(movementClass: MovementClass) {
        movementClassRepository.delete(movementClass)
    }
}
