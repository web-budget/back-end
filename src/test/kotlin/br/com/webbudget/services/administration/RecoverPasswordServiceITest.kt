package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.exceptions.InvalidPasswordRecoverTokenException
import br.com.webbudget.domain.services.administration.RecoverPasswordService
import br.com.webbudget.infrastructure.repository.administration.PasswordRecoverAttemptRepository
import br.com.webbudget.utilities.memoryLogAppender
import br.com.webbudget.utilities.startMemoryLogAppender
import br.com.webbudget.utilities.stopMemoryLogAppender
import ch.martinelli.oss.testcontainers.mailpit.MailpitClient
import ch.martinelli.oss.testcontainers.mailpit.assertions.MailpitAssertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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

class RecoverPasswordServiceITest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mailpitClient: MailpitClient

    @Autowired
    private lateinit var passwordRecoverAttemptRepository: PasswordRecoverAttemptRepository

    @Autowired
    private lateinit var recoverPasswordService: RecoverPasswordService

    @ParameterizedTest
    @MethodSource("usersToTest")
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should send recover password e-mail when user exists and register attempt`(
        userEmail: String,
        expectedSubject: String
    ) {
        mailpitClient.deleteAllMessages()

        recoverPasswordService.registerRecoveryAttempt(userEmail)

        await atMost 10.seconds untilAsserted {
            assertThat(mailpitClient.messageCount).isOne
        }

        val message = mailpitClient.allMessages.first()

        MailpitAssertions.assertThat(message)
            .isFrom("noreply@webbudget.com.br")
            .hasSubject(expectedSubject)
            .hasRecipient(userEmail)
            .hasRecipientCount(1)

        val attempts = passwordRecoverAttemptRepository.findByUserEmail(userEmail)
        assertThat(attempts).hasSize(1)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should ignore password recover request when user is not found`() {

        startMemoryLogAppender()

        val userEmail = "unknown@webbudget.com.br"
        val expectedLogMessage = "No user found with e-mail [${userEmail}], ignoring password recover request"

        recoverPasswordService.registerRecoveryAttempt(userEmail)

        assertThat(memoryLogAppender.countBy(expectedLogMessage)).isOne()

        stopMemoryLogAppender()
    }

    @Test
    fun `should fail if recover password token is not valid`() {

        val userEmail = "user@webbudget.com.br"

        assertThatThrownBy { recoverPasswordService.recover("s3cr3t", UUID.randomUUID(), userEmail) }
            .isInstanceOf(InvalidPasswordRecoverTokenException::class.java)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should change the password and mark recover attempt as used`() {

        val userEmail = "user@webbudget.com.br"

        recoverPasswordService.registerRecoveryAttempt(userEmail)

        val unusedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        recoverPasswordService.recover("s3cr3t", unusedAttempt.token, userEmail)

        val usedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        assertThat(usedAttempt.used).isTrue()
        assertThat(usedAttempt.user.password).isNotEqualTo(unusedAttempt.user.password)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-user.sql")
    fun `should fail if try to use same token to change password again`() {

        val userEmail = "user@webbudget.com.br"

        recoverPasswordService.registerRecoveryAttempt(userEmail)

        val unusedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        recoverPasswordService.recover("s3cr3t", unusedAttempt.token, userEmail)

        val usedAttempt = passwordRecoverAttemptRepository.findByUserEmail(userEmail).first()

        assertThat(usedAttempt.used).isTrue()
        assertThat(usedAttempt.user.password).isNotEqualTo(unusedAttempt.user.password)

        assertThatThrownBy { recoverPasswordService.recover("s3cr3t", unusedAttempt.token, userEmail) }
            .isInstanceOf(InvalidPasswordRecoverTokenException::class.java)
    }

    companion object {

        @JvmStatic
        fun usersToTest(): Stream<Arguments> = Stream.of(
            Arguments.of("other-user@webbudget.com.br", "Password recovery"),
            Arguments.of("user@webbudget.com.br", "Recuperação de senha")
        )
    }
}
