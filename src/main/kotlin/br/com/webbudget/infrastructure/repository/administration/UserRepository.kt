package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : DefaultRepository<User> {

    fun findByEmail(username: String?): User?

    fun findByEmailAndExternalIdNot(email: String, externalId: UUID): User?
}
