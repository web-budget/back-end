package br.com.webbudget.infrastructure.repository.configuration

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : DefaultRepository<User> {

    fun findByEmail(username: String?): User?
}
