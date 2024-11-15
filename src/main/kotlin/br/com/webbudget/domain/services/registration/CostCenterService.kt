package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.CostCenterValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CostCenterService(
    private val costCenterRepository: CostCenterRepository,
    @OnCreateValidation
    private val creationValidators: List<CostCenterValidator>,
    @OnUpdateValidation
    private val updateValidators: List<CostCenterValidator>
) {

    @Transactional
    fun create(costCenter: CostCenter): UUID {

        creationValidators.forEach { it.validate(costCenter) }

        val created = costCenterRepository.persist(costCenter)
        return created.externalId!!
    }

    @Transactional
    fun update(costCenter: CostCenter): CostCenter {

        updateValidators.forEach { it.validate(costCenter) }

        return costCenterRepository.merge(costCenter)
    }

    @Transactional
    fun delete(costCenter: CostCenter) {
        costCenterRepository.delete(costCenter)
    }
}
