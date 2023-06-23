package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.events.UserCreatedEvent
import br.com.webbudget.domain.exceptions.InvalidAccountActivationTokenException
import br.com.webbudget.domain.services.administration.AccountActivationService
import br.com.webbudget.infrastructure.repository.administration.AccountActivationAttemptRepository
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import java.util.concurrent.TimeUnit.SECONDS

class AccountActivationServiceTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var accountActivationService: AccountActivationService

    @Autowired
    private lateinit var accountActivationAttemptRepository: AccountActivationAttemptRepository

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should listen to the user creation event and send activation e-mail`() {

        val event = UserCreatedEvent("user@webbudget.com.br")

        accountActivationService.requestActivation(event)

        assertThat(greenMail.waitForIncomingEmail(3000, 1)).isTrue()
        assertThat(greenMail.receivedMessages[0]).satisfies({
            assertThat(it.allRecipients[0].toString()).isEqualTo(event.userName)
        })

        val attempts = accountActivationAttemptRepository.findByUserEmail(event.userName)

        assertThat(attempts).isNotEmpty.hasSize(1)
    }

    @Test
    fun `should fail unknown user is provided`() {

        val event = UserCreatedEvent("unknown@webbudget.com.br")
        val expectedLogMessage = "Can't find any account with username [${event.userName}], ignoring event"

        startMemoryLoggerAppender()

        accountActivationService.requestActivation(event)

        await.atMost(3, SECONDS) untilAsserted {
            assertThat(memoryAppender.countBy(expectedLogMessage)).isOne()
        }

        stopMemoryLoggerAppender()
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should activate account and mark token as activated`() {

        val event = UserCreatedEvent("user@webbudget.com.br")

        accountActivationService.requestActivation(event)

        await.atMost(5, SECONDS) untilAsserted {
            val attempts = accountActivationAttemptRepository.findByUserEmail(event.userName)
            assertThat(attempts).isNotEmpty
        }

        val attempt = accountActivationAttemptRepository.findByUserEmail(event.userName).first()
        accountActivationService.activate(attempt.token, event.userName)

        assertThat(
            accountActivationAttemptRepository.findByTokenAndUserEmailAndActivatedOnIsNull(
                attempt.token, event.userName
            )
        ).isNull()
    }

    @Test
    fun `should fail when activating with an unknown token or account`() {

        assertThatThrownBy {
            accountActivationService.activate(
                UUID.randomUUID(),
                "unknown@webbudget.com.br"
            )
        }.isInstanceOf(InvalidAccountActivationTokenException::class.java)
            .hasMessage("account-activation.errors.invalid-token")
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-dummy-user.sql")
    fun `should fail when try to activate an already activated token`() {

        val event = UserCreatedEvent("user@webbudget.com.br")

        accountActivationService.requestActivation(event)

        await.atMost(5, SECONDS) untilAsserted {
            val attempts = accountActivationAttemptRepository.findByUserEmail(event.userName)
            assertThat(attempts).isNotEmpty
        }

        val attempt = accountActivationAttemptRepository.findByUserEmail(event.userName).first()
        accountActivationService.activate(attempt.token, event.userName)

        assertThatThrownBy { accountActivationService.activate(attempt.token, event.userName) }
            .isInstanceOf(InvalidAccountActivationTokenException::class.java)
            .hasMessage("account-activation.errors.invalid-token")
    }

    companion object {

        @JvmStatic
        @RegisterExtension
        val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP).withConfiguration(
            GreenMailConfiguration.aConfig().withUser("mail_user", "mail_password")
        ).withPerMethodLifecycle(false)
    }
}
