package br.com.webbudget.backend.infrastructure.repository.configuration

import br.com.webbudget.backend.domain.entities.configuration.Authority
import br.com.webbudget.backend.domain.entities.configuration.Grant
import br.com.webbudget.backend.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : DefaultRepository<Authority> {
}