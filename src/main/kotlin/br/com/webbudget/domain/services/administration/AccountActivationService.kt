package br.com.webbudget.domain.services.administration

import br.com.webbudget.domain.entities.administration.AccountActivationAttempt
import br.com.webbudget.domain.exceptions.InvalidAccountActivationTokenException
import br.com.webbudget.domain.mail.AccountActivationEmail
import br.com.webbudget.domain.services.MailSenderService
import br.com.webbudget.infrastructure.repository.administration.AccountActivationAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class AccountActivationService(
    @Value("\${web-budget.front-end-url}")
    private val frontendUrl: String,
    private val userRepository: UserRepository,
    private val mailSenderService: MailSenderService,
    private val accountActivationAttemptRepository: AccountActivationAttemptRepository
) {

    @Transactional
    fun requestActivation(userName: String) {

        val user = userRepository.findByEmail(userName)

        if (user == null) {
            logger.warn { "Can't find any account with username [$userName], ignoring event" }
            return
        }

        val activationAttempt = AccountActivationAttempt(UUID.randomUUID(), user)
        accountActivationAttemptRepository.merge(activationAttempt)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val recoverPasswordUrl = "${frontendUrl}/login/account-activation" +
                "?token=${activationAttempt.token}&email=${user.email}"

        val mailMessage = AccountActivationEmail(user)
        mailMessage.addVariable("activateAccountUrl", recoverPasswordUrl)
        mailMessage.addVariable("validUntil", activationAttempt.validity.format(formatter))

        mailSenderService.sendEmail(mailMessage)
    }

    @Transactional
    fun activate(token: UUID, userEmail: String) {

        val attempt = accountActivationAttemptRepository.findByTokenAndUserEmailAndActivatedOnIsNull(token, userEmail)
            ?: throw InvalidAccountActivationTokenException(userEmail)

        if (attempt.validity.isBefore(LocalDateTime.now())) {
            logger.debug { "Account activation token has expired on [${attempt.validity}]" }
            throw InvalidAccountActivationTokenException(userEmail)
        }

        attempt
            .apply { this.activatedOn = LocalDateTime.now() }
            .also { accountActivationAttemptRepository.merge(it) }
            .also {
                it.user.active = true
                userRepository.merge(it.user)
            }
    }
}
