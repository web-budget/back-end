package br.com.webbudget.infrastructure.repository.configuration

import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GrantRepository : DefaultRepository<Grant> {

    fun deleteByUserExternalId(userExternalId: UUID)
}
