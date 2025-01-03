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

        validateBeforeCreteOrUpdate(recurringMovement)

        return recurringMovementRepository.persist(recurringMovement).externalId!!
    }

    @Transactional
    fun update(recurringMovement: RecurringMovement): RecurringMovement {

        validateBeforeCreteOrUpdate(recurringMovement)

        val externalId = requireNotNull(recurringMovement.externalId) { EXTERNAL_ID_IS_NULL }

        apportionmentRepository.deleteByPeriodMovementExternalId(externalId)

        recurringMovement.apportionments.forEach {
            it.recurringMovement = recurringMovement
            apportionmentRepository.persist(it)
        }

        return recurringMovement
    }

    @Transactional
    fun delete(recurringMovement: RecurringMovement) {
        recurringMovementRepository.delete(recurringMovement)
    }

    private fun validateBeforeCreteOrUpdate(recurringMovement: RecurringMovement) {
        ensure(recurringMovement.apportionments.sumEqualTo(recurringMovement.value)) {
            throw BusinessException(
                "Apportionments total must be equal to movement value",
                "period-movement.errors.invalid-apportionments"
            )
        }
    }
}