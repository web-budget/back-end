package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.UserAccountController
import br.com.webbudget.domain.exceptions.InvalidPasswordRecoverTokenException
import br.com.webbudget.domain.services.administration.RecoverPasswordService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.patch
import java.util.UUID

@WebMvcTest(UserAccountController::class)
class UserAccountControllerTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var recoverPasswordService: RecoverPasswordService

    @Test
    fun `should call forgot password and get accepted`() {

        val userEmail = "some@user.com"

        val body = "{\"email\": \"$userEmail\"}"

        every { recoverPasswordService.registerRecoveryAttempt(userEmail) } just runs

        mockMvc.patch("$ENDPOINT_URL/forgot-password") {
            with(csrf())
            content = body
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isAccepted() }
        }

        verify(exactly = 1) { recoverPasswordService.registerRecoveryAttempt(userEmail) }

        confirmVerified(recoverPasswordService)
    }

    @Test
    fun `should call forgot password and get unprocessable entity when email is empty`() {

        val body = "{\"email\": \"\"}"

        mockMvc.patch("$ENDPOINT_URL/forgot-password") {
            with(csrf())
            content = body
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnprocessableEntity() }
        }
    }

    @Test
    fun `should call recover password and receive ok if token is valid`() {

        val token = UUID.randomUUID()
        val userEmail = "some@user.com"
        val newPassword = "s3cr3t"

        val body = "{" +
                "\"token\": \"$token\"," +
                "\"email\": \"$userEmail\"," +
                "\"password\": \"$newPassword\"" +
                "}"

        every { recoverPasswordService.recover(newPassword, token, userEmail) } just runs

        mockMvc.patch("$ENDPOINT_URL/recover-password") {
            with(csrf())
            content = body
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { recoverPasswordService.recover(newPassword, token, userEmail) }

        confirmVerified(recoverPasswordService)
    }

    @Test
    fun `should call recover password and receive bad request if token is invalid`() {

        val token = UUID.randomUUID()
        val userEmail = "some@user.com"
        val newPassword = "s3cr3t"

        val body = "{" +
                "\"token\": \"$token\"," +
                "\"email\": \"$userEmail\"," +
                "\"password\": \"$newPassword\"" +
                "}"

        every { recoverPasswordService.recover(newPassword, token, userEmail) } throws
                InvalidPasswordRecoverTokenException(userEmail)

        mockMvc.patch("$ENDPOINT_URL/recover-password") {
            with(csrf())
            content = body
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 1) { recoverPasswordService.recover(newPassword, token, userEmail) }

        confirmVerified(recoverPasswordService)
    }

    @Test
    fun `should call recover password and unprocessable entity if toke, email or password are empty`() {

        val body = "{" +
                "\"token\": \"\"," +
                "\"email\": \"\"," +
                "\"password\": \"\"" +
                "}"

        mockMvc.patch("$ENDPOINT_URL/recover-password") {
            with(csrf())
            content = body
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnprocessableEntity() }
        }
    }

    // TODO add tests to the account activation flow

    companion object {
        private const val ENDPOINT_URL = "/user-account"
    }
}
