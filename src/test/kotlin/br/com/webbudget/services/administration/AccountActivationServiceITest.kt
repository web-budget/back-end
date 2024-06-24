package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.InvalidAccountActivationTokenException
import br.com.webbudget.domain.services.administration.AccountActivationService
import br.com.webbudget.infrastructure.repository.administration.AccountActivationAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

class AccountActivationServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var accountActivationService: AccountActivationService

    @Autowired
    private lateinit var accountActivationAttemptRepository: AccountActivationAttemptRepository

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should send activation e-mail`() {

        val userName = "user@webbudget.com.br"
        accountActivationService.requestActivation(userName)

        assertThat(greenMail.waitForIncomingEmail(20000, 1)).isTrue()
        assertThat(greenMail.receivedMessages[0]).satisfies({
            assertThat(it.allRecipients[0].toString()).isEqualTo(userName)
        })

        val attempts = accountActivationAttemptRepository.findByUserEmail(userName)

        assertThat(attempts).isNotEmpty.hasSize(1)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should activate account, mark token as activated and user as active`() {

        val userName = "user@webbudget.com.br"
        accountActivationService.requestActivation(userName)

        val attempt = accountActivationAttemptRepository.findByUserEmail(userName).first()
        accountActivationService.activate(attempt.token, userName)

        assertThat(
            accountActivationAttemptRepository.findByTokenAndUserEmailAndActivatedOnIsNull(attempt.token, userName)
        ).isNull()

        val user = userRepository.findByEmail(userName) ?: fail(OBJECT_NOT_FOUND_ERROR)
        assertThat(user.active).isTrue()
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
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should fail when try to activate an already activated token`() {

        val userName = "user@webbudget.com.br"
        accountActivationService.requestActivation(userName)

        val attempt = accountActivationAttemptRepository.findByUserEmail(userName).first()
        accountActivationService.activate(attempt.token, userName)

        assertThatThrownBy { accountActivationService.activate(attempt.token, userName) }
            .isInstanceOf(InvalidAccountActivationTokenException::class.java)
            .hasMessage("account-activation.errors.invalid-token")
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
