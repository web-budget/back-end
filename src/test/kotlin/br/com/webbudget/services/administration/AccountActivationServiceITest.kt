package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.InvalidAccountActivationTokenException
import br.com.webbudget.domain.services.administration.AccountActivationService
import br.com.webbudget.infrastructure.repository.administration.AccountActivationAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import ch.martinelli.oss.testcontainers.mailpit.MailpitClient
import ch.martinelli.oss.testcontainers.mailpit.assertions.MailpitAssertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.fail
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import java.util.stream.Stream
import kotlin.time.Duration.Companion.seconds

class AccountActivationServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mailpitClient: MailpitClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var accountActivationService: AccountActivationService

    @Autowired
    private lateinit var accountActivationAttemptRepository: AccountActivationAttemptRepository

    @ParameterizedTest
    @MethodSource("usersToTest")
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should send activation e-mail`(
        userEmail: String,
        expectedSubject: String
    ) {
        mailpitClient.deleteAllMessages()

        accountActivationService.requestActivation(userEmail)

        await atMost 10.seconds untilAsserted {
            assertThat(mailpitClient.messageCount).isOne
        }

        val message = mailpitClient.allMessages.first()

        MailpitAssertions.assertThat(message)
            .isFrom("noreply@webbudget.com.br")
            .hasSubject(expectedSubject)
            .hasRecipient(userEmail)
            .hasRecipientCount(1)

        val attempts = accountActivationAttemptRepository.findByUserEmail(userEmail)
        assertThat(attempts).hasSize(1)
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
    }

    companion object {

        @JvmStatic
        fun usersToTest(): Stream<Arguments> = Stream.of(
            Arguments.of("other-user@webbudget.com.br", "User account activation"),
            Arguments.of("user@webbudget.com.br", "Ativar conta de usuário")
        )
    }
}
