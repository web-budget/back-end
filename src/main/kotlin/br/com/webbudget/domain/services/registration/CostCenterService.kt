package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.validators.CreationValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.domain.validators.registration.CostCenterValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CostCenterService(
    private val costCenterRepository: CostCenterRepository,
    @CreationValidation
    private val creatingValidators: List<CostCenterValidator>,
    @UpdatingValidation
    private val updatingValidation: List<CostCenterValidator>
) {

    @Transactional
    fun create(costCenter: CostCenter): CostCenter {
        creatingValidators.forEach { it.validate(costCenter) }
        return costCenterRepository.save(costCenter)
    }

    @Transactional
    fun update(costCenter: CostCenter): CostCenter {
        updatingValidation.forEach { it.validate(costCenter) }
        return costCenterRepository.save(costCenter)
    }

    @Transactional
    fun delete(costCenter: CostCenter) {
        costCenterRepository.delete(costCenter)
    }
}