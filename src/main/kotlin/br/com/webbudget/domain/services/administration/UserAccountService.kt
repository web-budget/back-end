package br.com.webbudget.domain.services.administration

import br.com.webbudget.domain.entities.administration.PasswordRecoverAttempt
import br.com.webbudget.domain.exceptions.InvalidPasswordRecoverTokenException
import br.com.webbudget.domain.mail.RecoverPasswordEmail
import br.com.webbudget.domain.services.MailSenderService
import br.com.webbudget.infrastructure.repository.administration.PasswordRecoverAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import io.github.oshai.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class UserAccountService(
    @Value("\${web-budget.front-end-url}")
    private val frontendUrl: String,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val mailSenderService: MailSenderService,
    private val passwordRecoverAttemptRepository: PasswordRecoverAttemptRepository
) {

    @Transactional
    fun recoverPassword(email: String) {

        val user = userRepository.findByEmail(email)

        if (user == null) {
            logger.warn { "No user found with e-mail [${email}], ignoring password recover request" }
            return
        }

        val recoverAttempt = PasswordRecoverAttempt(UUID.randomUUID(), user)
        passwordRecoverAttemptRepository.persist(recoverAttempt)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val recoverPasswordUrl = "${frontendUrl}/login/recover-password" +
                "?token=${recoverAttempt.token}&email=${user.email}"

        val mailMessage = RecoverPasswordEmail(user)
        mailMessage.addVariable("recoverPasswordUrl", recoverPasswordUrl)
        mailMessage.addVariable("validUntil", recoverAttempt.validity.format(formatter))

        mailSenderService.sendEmail(mailMessage)
    }

    @Transactional
    fun changePassword(newPassword: String, token: UUID, userEmail: String) {

        val recoverAttempt = passwordRecoverAttemptRepository.findByTokenAndUserEmailAndUsedFalse(token, userEmail)
            ?: throw InvalidPasswordRecoverTokenException(userEmail)

        if (recoverAttempt.validity.isBefore(LocalDateTime.now())) {
            logger.debug { "Recover password toke has expired on [${recoverAttempt.validity}]" }
            throw InvalidPasswordRecoverTokenException(userEmail)
        }

        userService.updatePassword(recoverAttempt.user, newPassword, false)

        recoverAttempt
            .apply { this.used = true }
            .also { passwordRecoverAttemptRepository.merge(it) }
    }
}
