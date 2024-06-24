package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.TokenController
import br.com.webbudget.domain.services.administration.TokenService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.createUser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(TokenController::class)
class TokenControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var tokenService: TokenService

    @Test
    @WithMockUser(username = "user@test.com", password = "admin", roles = ["ADMINISTRATION"])
    fun `should generate the token`() {

        val userEmail = "user@test.com"
        val userPassword = "admin"
        val authorities = arrayOf("ROLE_ADMINISTRATION")
        val expectedUser = createUser(password = userPassword, authorities = authorities)
            .apply { this.active = true }

        every { userRepository.findByEmail(userEmail) } returns expectedUser
        every { tokenService.generateFor(userEmail, authorities.asList()) } returns UUID.randomUUID().toString()

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
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
        verify(exactly = 1) { tokenService.generateFor(userEmail, authorities.toList()) }

        confirmVerified(userRepository, tokenService)
    }

    companion object {
        private const val ENDPOINT_URL = "/token"
    }
}
