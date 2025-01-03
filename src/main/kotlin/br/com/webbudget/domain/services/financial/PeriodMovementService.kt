package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.financial.sumEqualTo
import br.com.webbudget.domain.exceptions.BusinessException
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
            throw BusinessException("Period movement is accounted", "period-movement.errors.accounted-movement")
        }

        periodMovementRepository.delete(periodMovement)
    }

    private fun validateBeforeCreteOrUpdate(periodMovement: PeriodMovement) {

        ensure(periodMovement.apportionments.sumEqualTo(periodMovement.value)) {
            throw BusinessException(
                "Apportionments total must be equal to movement value",
                "period-movement.errors.invalid-apportionments"
            )
        }

        ensure(periodMovement.financialPeriod.isOpen()) {
            BusinessException("Financial period is not open", "period-movement.errors.period-not-open")
        }

        ensure(periodMovement.isAccounted().not()) {
            BusinessException("Period movement is not open", "period-movement.errors.not-open")
        }
    }
}