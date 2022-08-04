package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CostCenterRepository : DefaultRepository<CostCenter> {

    fun findByNameIgnoreCase(description: String): CostCenter?

    fun findByNameIgnoreCaseAndExternalIdNot(description: String, externalId: UUID): CostCenter?
}