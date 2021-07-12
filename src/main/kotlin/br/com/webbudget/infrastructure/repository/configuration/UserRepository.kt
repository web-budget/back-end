package br.com.webbudget.infrastructure.repository.configuration

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : DefaultRepository<User> {

    fun findByEmail(username: String?): User?

    fun findByEmailAndExternalIdNot(email: String, externalId: UUID): User?
}
