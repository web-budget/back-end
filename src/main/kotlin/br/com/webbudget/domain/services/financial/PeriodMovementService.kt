package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.financial.sumEqualTo
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ErrorCodes.ACCOUNTED_PERIOD_MOVEMENT
import br.com.webbudget.domain.exceptions.ErrorCodes.FINANCIAL_PERIOD_NOT_OPEN
import br.com.webbudget.domain.exceptions.ErrorCodes.INVALID_APPORTIONMENTS
import br.com.webbudget.infrastructure.repository.financial.ApportionmentRepository
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
import br.com.webbudget.infrastructure.utilities.CommonErrorMessages.EXTERNAL_ID_IS_NULL
import br.com.webbudget.infrastructure.utilities.ensure
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PeriodMovementService(
    private val apportionmentRepository: ApportionmentRepository,
    private val periodMovementRepository: PeriodMovementRepository,
) {

    @Transactional
    fun create(periodMovement: PeriodMovement): UUID {

        validateBeforeCreteOrUpdate(periodMovement)

        return periodMovementRepository.persist(periodMovement).externalId!!
    }

    @Transactional
    fun update(periodMovement: PeriodMovement): PeriodMovement {

        validateBeforeCreteOrUpdate(periodMovement)

        val externalId = requireNotNull(periodMovement.externalId) { EXTERNAL_ID_IS_NULL }

        apportionmentRepository.deleteByPeriodMovementExternalId(externalId)

        periodMovement.apportionments.forEach {
            it.periodMovement = periodMovement
            apportionmentRepository.persist(it)
        }

        return periodMovementRepository.merge(periodMovement)
    }

    @Transactional
    fun delete(periodMovement: PeriodMovement) {

        ensure(periodMovement.isAccounted().not()) {
            throw BusinessException("Period movement is accounted", ACCOUNTED_PERIOD_MOVEMENT)
        }

        periodMovementRepository.delete(periodMovement)
    }

    private fun validateBeforeCreteOrUpdate(periodMovement: PeriodMovement) {

        ensure(periodMovement.apportionments.sumEqualTo(periodMovement.value)) {
            throw BusinessException("Apportionments total must be equal to movement value", INVALID_APPORTIONMENTS)
        }

        ensure(periodMovement.financialPeriod.isOpen()) {
            BusinessException("Financial period is not open", FINANCIAL_PERIOD_NOT_OPEN)
        }

        ensure(periodMovement.isAccounted().not()) {
            BusinessException("Period movement is accounted", ACCOUNTED_PERIOD_MOVEMENT)
        }
    }
}