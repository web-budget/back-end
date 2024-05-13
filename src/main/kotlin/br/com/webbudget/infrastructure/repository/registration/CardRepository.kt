package br.com.webbudget.infrastructure.repository.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CardRepository : BaseRepository<Card> {

    fun findByTypeAndLastFourDigits(type: Card.Type, lastFourDigits: String): Card?

    fun findByTypeAndLastFourDigitsAndExternalIdNot(type: Card.Type, lastFourDigits: String, externalId: UUID): Card?

    object Specifications : SpecificationHelpers {

        fun byName(value: String?) = Specification<Card> { root, _, builder ->
            value?.let { builder.like(builder.lower(root["name"]), likeIgnoreCase(value)) }
        }

        fun byLastFourDigits(value: String?) = Specification<Card> { root, _, builder ->
            value?.let { builder.like(builder.lower(root["lastFourDigits"]), likeIgnoreCase(value)) }
        }

        fun byFlag(value: String?) = Specification<Card> { root, _, builder ->
            value?.let { builder.like(builder.lower(root["flag"]), likeIgnoreCase(value)) }
        }

        fun byActive(value: Boolean?) = Specification<Card> { root, _, builder ->
            value?.let { builder.equal(root.get<Boolean>("active"), value) }
        }
    }
}
