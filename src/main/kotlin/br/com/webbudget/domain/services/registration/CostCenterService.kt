package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CostCenterService(
    private val costCenterRepository: CostCenterRepository
) {

    @Transactional
    fun create(costCenter: CostCenter): CostCenter {
        // TODO check for duplicated names
        return costCenterRepository.save(costCenter)
    }

    @Transactional
    fun update(costCenter: CostCenter): CostCenter {
        // TODO check for duplicated names
        return costCenterRepository.save(costCenter)
    }

    @Transactional
    fun delete(costCenter: CostCenter) {
        // TODO check if is being used
        costCenterRepository.delete(costCenter)
    }
}