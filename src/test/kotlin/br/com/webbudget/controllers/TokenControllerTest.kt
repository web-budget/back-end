package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.TokenController.TokenResponse
import br.com.webbudget.domain.services.administration.AuthenticationService
import br.com.webbudget.domain.services.administration.AuthenticationService.AuthenticableUser
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.UserFixture
import com.nimbusds.jwt.JWTParser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.util.concurrent.TimeUnit

@TestPropertySource(
    properties = [
        "web-budget.jwt.access-token-expiration=1"
    ]
)
class TokenControllerTest : BaseControllerIntegrationTest() {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var authenticationService: AuthenticationService

    @Test
    fun `should generate the token`() {

        val userEmail = "user@test.com"
        val userPassword = "admin"
        val expectedUser = UserFixture.create(passwordEncoder.encode(userPassword), "ADMINISTRATION")

        every { authenticationService.loadUserByUsername(userEmail) } returns AuthenticableUser.from(expectedUser)
        every { userRepository.findByEmail(userEmail) } returns expectedUser

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(httpBasic(userEmail, userPassword))
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse) {
            isObject
            node("token").isNotNull
            node("email").isNotNull.isEqualTo(userEmail)
            node("name").isNotNull.isEqualTo("User")
        }

        verify(exactly = 1) { userRepository.findByEmail(userEmail) }
        verify(exactly = 1) { authenticationService.loadUserByUsername(userEmail) }

        confirmVerified(userRepository, authenticationService)
    }

    @Test
    fun `should generate token that will expire soon`() {

        val userEmail = "user@test.com"
        val userPassword = "admin"
        val expectedUser = UserFixture.create(passwordEncoder.encode(userPassword), "ADMINISTRATION")

        every { authenticationService.loadUserByUsername(userEmail) } returns AuthenticableUser.from(expectedUser)
        every { userRepository.findByEmail(userEmail) } returns expectedUser

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(httpBasic(userEmail, userPassword))
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        val tokenResponse = jsonToObject(jsonResponse, TokenResponse::class.java)

        val expiration = JWTParser.parse(tokenResponse.token).jwtClaimsSet.expirationTime
        await().atMost(2, TimeUnit.SECONDS).untilAsserted() {
            assertThat(expiration).isBefore(Instant.now())
        }

        verify(exactly = 1) { userRepository.findByEmail(userEmail) }
        verify(exactly = 1) { authenticationService.loadUserByUsername(userEmail) }

        confirmVerified(userRepository, authenticationService)
    }

    companion object {
        private const val ENDPOINT_URL = "/token"
    }
}
