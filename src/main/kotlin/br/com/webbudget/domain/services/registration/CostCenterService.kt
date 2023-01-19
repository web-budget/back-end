package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CostCenterService(
    private val costCenterRepository: CostCenterRepository,
    private val costCenterValidationService: CostCenterValidationService
) {

    @Transactional
    fun create(costCenter: CostCenter): UUID {
        costCenterValidationService.validateOnCreate(costCenter)
        val created = costCenterRepository.persist(costCenter)
        return created.externalId!!
    }

    @Transactional
    fun update(costCenter: CostCenter): CostCenter {
        costCenterValidationService.validateOnUpdate(costCenter)
        return costCenterRepository.merge(costCenter)
    }

    @Transactional
    fun delete(costCenter: CostCenter) {
        costCenterRepository.delete(costCenter)
    }
}
