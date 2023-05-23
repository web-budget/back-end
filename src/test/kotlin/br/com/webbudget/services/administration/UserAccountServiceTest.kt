package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.InvalidPasswordRecoverTokenException
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.infrastructure.repository.administration.PasswordRecoverAttemptRepository
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

class UserAccountServiceTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var passwordRecoverAttemptRepository: PasswordRecoverAttemptRepository

    @Autowired
    private lateinit var userAccountService: UserAccountService

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should send recover password e-mail when user exists and register attempt`() {

        val userEmail = "user@webbudget.com.br"
        userAccountService.recoverPassword(userEmail)

        assertThat(greenMail.waitForIncomingEmail(3000, 1)).isTrue()
        assertThat(greenMail.receivedMessages[0])
            .satisfies({
                assertThat(it.allRecipients[0].toString()).isEqualTo(userEmail)
            })

        val attempts = passwordRecoverAttemptRepository.findByUserEmail(userEmail)

        assertThat(attempts)
            .isNotEmpty
            .hasSize(1)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should not send recover password e-mail when user does not exists`() {

        startMemoryLoggerAppender()

        val userEmail = "other_user@webbudget.com.br"
        val expectedLogMessage = "No user found with e-mail [${userEmail}], ignoring password recover request"

        userAccountService.recoverPassword(userEmail)

        assertThat(greenMail.receivedMessages).isEmpty()
        assertThat(memoryAppender.countBy(expectedLogMessage)).isEqualTo(1)

        stopMemoryLoggerAppender()
    }

    @Test
    fun `should fail if recover password token is not valid`() {

        val userEmail = "user@webbudget.com.br"

        assertThatThrownBy { userAccountService.changePassword("s3cr3t", UUID.randomUUID(), userEmail) }
            .isInstanceOf(InvalidPasswordRecoverTokenException::class.java)
            .hasMessage("recover-password.errors.invalid-token")
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should change the password and mark recover attempt as used`() {

        val userEmail = "user@webbudget.com.br"

        userAccountService.recoverPassword(userEmail)

        val unusedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        userAccountService.changePassword("s3cr3t", unusedAttempt.token, userEmail)

        val usedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        assertThat(usedAttempt.used).isTrue()
        assertThat(usedAttempt.user.password).isNotEqualTo(unusedAttempt.user.password)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should fail if try to use same token to change password again`() {

        val userEmail = "user@webbudget.com.br"

        userAccountService.recoverPassword(userEmail)

        val unusedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        userAccountService.changePassword("s3cr3t", unusedAttempt.token, userEmail)

        val usedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        assertThat(usedAttempt.used).isTrue()
        assertThat(usedAttempt.user.password).isNotEqualTo(unusedAttempt.user.password)

        assertThatThrownBy { userAccountService.changePassword("s3cr3t", unusedAttempt.token, userEmail) }
            .isInstanceOf(InvalidPasswordRecoverTokenException::class.java)
            .hasMessage("recover-password.errors.invalid-token")
    }

    companion object {

        @JvmStatic
        @RegisterExtension
        val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(
                GreenMailConfiguration.aConfig()
                    .withUser("mail_user", "mail_password")
            )
            .withPerMethodLifecycle(false)
    }
}
