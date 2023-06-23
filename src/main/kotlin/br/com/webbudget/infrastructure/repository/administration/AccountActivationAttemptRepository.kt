package br.com.webbudget.infrastructure.repository.administration

import br.com.webbudget.domain.entities.administration.AccountActivationAttempt
import br.com.webbudget.infrastructure.repository.DefaultRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AccountActivationAttemptRepository : DefaultRepository<AccountActivationAttempt> {

    fun findByUserEmail(email: String): List<AccountActivationAttempt>

    fun findByTokenAndUserEmailAndActivatedOnIsNull(token: UUID, email: String): AccountActivationAttempt?
}
