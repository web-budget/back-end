package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository

@Repository
interface CostCenterRepository : DefaultRepository<CostCenter> {

    fun findByDescriptionIgnoreCase(description: String): CostCenter?
}