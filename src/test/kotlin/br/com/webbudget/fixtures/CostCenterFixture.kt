package br.com.webbudget.fixtures

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.boot.test.context.TestComponent

@TestComponent
class CostCenterFixture(
    private val costCenterRepository: CostCenterRepository
) {

    fun createCostCenter(description: String = "Test Cost Center", active: Boolean = true): CostCenter {
        val costCenter = CostCenter(description, active)
        return costCenterRepository.save(costCenter)
    }
}