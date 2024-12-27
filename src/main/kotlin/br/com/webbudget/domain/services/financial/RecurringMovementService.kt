package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.infrastructure.repository.financial.RecurringMovementRepository
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
        return UUID.randomUUID()
    }

    @Transactional
    fun update(recurringMovement: RecurringMovement): RecurringMovement {
        return recurringMovement
    }

    @Transactional
    fun delete(recurringMovement: RecurringMovement) {

    }
}