package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.infrastructure.repository.BaseRepository
import br.com.webbudget.infrastructure.repository.SpecificationHelpers
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : BaseRepository<User> {

    fun findByEmail(username: String?): User?

    fun findByEmailAndExternalIdNot(email: String, externalId: UUID): User?

    object Specifications : SpecificationHelpers {

        fun byName(name: String?) = Specification<User> { root, _, builder ->
            name?.let { builder.like(builder.lower(root["name"]), likeIgnoreCase(name)) }
        }

        fun byEmail(email: String?) = Specification<User> { root, _, builder ->
            email?.let { builder.like(builder.lower(root["email"]), likeIgnoreCase(email)) }
        }

        fun byActive(active: Boolean?) = Specification<User> { root, _, builder ->
            active?.let { builder.equal(root.get<Boolean>("active"), active) }
        }
    }
}
