package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CostCenterRepository : BaseRepository<CostCenter> {

    fun findByNameIgnoreCase(description: String): CostCenter?

    fun findByNameIgnoreCaseAndExternalIdNot(description: String, externalId: UUID): CostCenter?

    object Specifications : SpecificationHelpers {

        fun byName(name: String?) = Specification<CostCenter> { root, _, builder ->
            name?.let { builder.like(builder.lower(root["name"]), likeIgnoreCase(name)) }
        }

        fun byDescription(description: String?) = Specification<CostCenter> { root, _, builder ->
            description?.let { builder.like(builder.lower(root["description"]), likeIgnoreCase(description)) }
        }

        fun byActive(active: Boolean?) = Specification<CostCenter> { root, _, builder ->
            active?.let { builder.equal(root.get<Boolean>("active"), active) }
        }
    }
}
