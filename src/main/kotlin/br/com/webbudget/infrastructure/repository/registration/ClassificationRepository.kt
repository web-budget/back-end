package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.entities.registration.Classification.Type
import br.com.webbudget.domain.projections.registration.BudgetAllocated
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ClassificationRepository : BaseRepository<Classification> {

    fun findByNameIgnoreCase(description: String): Classification?

    fun findByNameIgnoreCaseAndExternalIdNot(name: String, externalId: UUID): Classification?

    @Query(
        """
        select coalesce(sum(mc.budget), 0.0) as total 
        from Classification mc 
        where mc.costCenter = :costCenter
        and mc.type = :type
    """
    )
    fun findBudgetAllocatedByCostCenter(costCenter: CostCenter, type: Type): BudgetAllocated

    object Specifications : SpecificationHelpers {

        fun byName(name: String?) = Specification<Classification> { root, _, builder ->
            name?.let { builder.like(builder.lower(root["name"]), likeIgnoreCase(name)) }
        }

        fun byDescription(description: String?) = Specification<Classification> { root, _, builder ->
            description?.let { builder.like(builder.lower(root["description"]), likeIgnoreCase(description)) }
        }

        fun byActive(active: Boolean?) = Specification<Classification> { root, _, builder ->
            active?.let { builder.equal(root.get<Boolean>("active"), active) }
        }
    }
}