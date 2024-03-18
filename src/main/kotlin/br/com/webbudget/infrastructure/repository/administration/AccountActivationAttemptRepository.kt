package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.AccountActivationAttempt
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AccountActivationAttemptRepository : BaseRepository<AccountActivationAttempt> {

    fun deleteByUserExternalId(externalId: UUID)

    fun findByUserEmail(email: String): List<AccountActivationAttempt>

    fun findByTokenAndUserEmailAndActivatedOnIsNull(token: UUID, email: String): AccountActivationAttempt?
}
