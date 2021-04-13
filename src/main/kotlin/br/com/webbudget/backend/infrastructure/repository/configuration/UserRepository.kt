package br.com.webbudget.backend.infrastructure.repository.configuration

import br.com.webbudget.backend.domain.entities.configuration.User
import br.com.webbudget.backend.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : DefaultRepository<User> {
}