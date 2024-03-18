package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.PasswordRecoverAttempt
import br.com.webbudget.infrastructure.repository.BaseRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PasswordRecoverAttemptRepository : BaseRepository<PasswordRecoverAttempt> {

    fun deleteByUserExternalId(externalId: UUID)

    fun findByTokenAndUserEmailAndUsedFalse(token: UUID, email: String): PasswordRecoverAttempt?

    fun findByUserEmail(email: String): List<PasswordRecoverAttempt>
}
