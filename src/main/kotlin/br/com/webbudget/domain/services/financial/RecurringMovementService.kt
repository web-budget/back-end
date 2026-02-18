package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.entities.financial.RecurringMovement.State.ENDED
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.infrastructure.repository.financial.RecurringMovementRepository
import br.com.webbudget.infrastructure.utilities.CommonErrorMessages.EXTERNAL_ID_IS_NULL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class RecurringMovementService(
    private val recurringMovementRepository: RecurringMovementRepository
) {

    @Transactional
    fun create(recurringMovement: RecurringMovement): UUID {
        return recurringMovementRepository.persist(recurringMovement).externalId!!
    }

    @Transactional
    fun update(recurringMovement: RecurringMovement): RecurringMovement {

        val externalId = requireNotNull(recurringMovement.externalId) { EXTERNAL_ID_IS_NULL }

        recurringMovementRepository.findByExternalIdAndState(externalId, ENDED)
            ?.let {
                throw BusinessException("Recurring movement isn't active", "recurring-movement.errors.invalid-state")
            }

        return recurringMovementRepository.merge(recurringMovement)
    }

    @Transactional
    fun delete(recurringMovement: RecurringMovement) {
        recurringMovementRepository.delete(recurringMovement)
    }
}