package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.entities.financial.sumEqualTo
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.infrastructure.repository.financial.ApportionmentRepository
import br.com.webbudget.infrastructure.repository.financial.RecurringMovementRepository
import br.com.webbudget.infrastructure.utilities.CommonErrorMessages.EXTERNAL_ID_IS_NULL
import br.com.webbudget.infrastructure.utilities.ensure
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class RecurringMovementService(
    private val apportionmentRepository: ApportionmentRepository,
    private val recurringMovementRepository: RecurringMovementRepository
) {

    @Transactional
    fun create(recurringMovement: RecurringMovement): UUID {
        validateApportionments(recurringMovement)
        return recurringMovementRepository.persist(recurringMovement).externalId!!
    }

    @Transactional
    fun update(recurringMovement: RecurringMovement): RecurringMovement {

        validateApportionments(recurringMovement)

        val externalId = requireNotNull(recurringMovement.externalId) { EXTERNAL_ID_IS_NULL }

        recurringMovementRepository.findByExternalIdAndState(externalId, RecurringMovement.State.ENDED)
            ?.let {
                throw BusinessException("Recurring movement is not active", "recurring-movement.errors.invalid-state")
            }

        apportionmentRepository.deleteByRecurringMovementExternalId(externalId)

        recurringMovement.apportionments.forEach {
            it.recurringMovement = recurringMovement
            apportionmentRepository.persist(it)
        }

        return recurringMovementRepository.merge(recurringMovement)
    }

    @Transactional
    fun delete(recurringMovement: RecurringMovement) {
        recurringMovementRepository.delete(recurringMovement)
    }

    private fun validateApportionments(recurringMovement: RecurringMovement) {
        ensure(recurringMovement.apportionments.sumEqualTo(recurringMovement.value)) {
            throw BusinessException(
                "Apportionments total must be equal to movement value",
                "recurring-movement.errors.invalid-apportionments"
            )
        }
    }
}