package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.PasswordRecoverAttempt
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PasswordRecoverAttemptRepository : DefaultRepository<PasswordRecoverAttempt> {

    fun findByTokenAndUser_email(token: UUID, email: String): PasswordRecoverAttempt?
}
