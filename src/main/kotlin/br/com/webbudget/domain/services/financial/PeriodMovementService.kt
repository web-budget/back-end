package br.com.webbudget.domain.services.financial

import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.financial.PeriodMovementValidator
import br.com.webbudget.infrastructure.utilities.ensure
import br.com.webbudget.infrastructure.repository.financial.ApportionmentRepository
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
import br.com.webbudget.infrastructure.utilities.CommonErrorMessages.EXTERNAL_ID_IS_NULL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PeriodMovementService(
    private val apportionmentRepository: ApportionmentRepository,
    private val periodMovementRepository: PeriodMovementRepository,
    @OnCreateValidation
    private val onCreateValidators: List<PeriodMovementValidator>,
    @OnUpdateValidation
    private val onUpdateValidation: List<PeriodMovementValidator>
) {

    @Transactional
    fun create(periodMovement: PeriodMovement): UUID {
        onCreateValidators.forEach { it.validate(periodMovement) }
        return periodMovementRepository.persist(periodMovement).externalId!!
    }

    @Transactional
    fun update(periodMovement: PeriodMovement): PeriodMovement {

        onUpdateValidation.forEach { it.validate(periodMovement) }

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
}