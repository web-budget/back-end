package br.com.webbudget.infrastructure.repository.configuration

import br.com.webbudget.domain.entities.configuration.Authority
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : DefaultRepository<Authority>
