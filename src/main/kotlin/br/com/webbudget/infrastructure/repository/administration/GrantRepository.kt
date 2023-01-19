package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface GrantRepository : DefaultRepository<Grant> {

    fun deleteByUserExternalId(userExternalId: UUID)

    fun findByUserExternalId(userExternalId: UUID): List<Grant>
}
