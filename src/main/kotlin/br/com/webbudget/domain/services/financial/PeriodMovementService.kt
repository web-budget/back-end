package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PeriodMovementService(
    private val periodMovementRepository: PeriodMovementRepository
) {

    @Transactional
    fun create(periodMovement: PeriodMovement): UUID {

        // TODO validate financial period is active
        // TODO validate apportionments

        return periodMovementRepository.persist(periodMovement).externalId!!
    }

    @Transactional
    fun update(periodMovement: PeriodMovement): PeriodMovement {

        // TODO validate financial period is active
        // TODO validate apportionments
        // TODO validate state, cant edit if not open

        return periodMovementRepository.merge(periodMovement)
    }

    @Transactional
    fun delete(periodMovement: PeriodMovement) {

        // TODO validate state, cant delete if not open
        // TODO if paid, must undo

        periodMovementRepository.delete(periodMovement)
    }
}