package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.infrastructure.repository.administration.PasswordRecoverAttemptRepository
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.concurrent.TimeUnit.SECONDS

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

        await()
            .atMost(5, SECONDS)
            .untilAsserted {
                assertThat(greenMail.receivedMessages.size).isEqualTo(1)
                assertThat(greenMail.receivedMessages[0])
                    .satisfies({
                        assertThat(it.allRecipients[0].toString()).isEqualTo(userEmail)
                    })
            }

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
