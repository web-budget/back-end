package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ClassificationRepository : BaseRepository<Classification> {

    fun findByNameIgnoreCase(description: String): Classification?

    fun findByNameIgnoreCaseAndExternalIdNot(name: String, externalId: UUID): Classification?

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