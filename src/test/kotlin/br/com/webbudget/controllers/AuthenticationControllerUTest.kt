package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.AuthenticationController
import br.com.webbudget.domain.services.administration.TokenService
import br.com.webbudget.utilities.fixtures.createUser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(AuthenticationController::class)
class AuthenticationControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var tokenService: TokenService

    @Test
    @WithMockUser(username = "user@test.com", password = "admin", roles = ["ADMINISTRATION"])
    fun `should login and generate the cookie with the token`() {

        val userEmail = "user@test.com"
        val roles = arrayOf("ROLE_ADMINISTRATION")

        val token = UUID.randomUUID().toString()

        every { tokenService.generate(userEmail, roles.asList()) } returns token

        mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
        }.andExpect {
            status { isOk() }
            cookie {
                exists("wb-auth")
                value("wb-auth", token)
                httpOnly("wb-auth", true)
                secure("wb-auth", true)
                maxAge("wb-auth", 3600)
                path("wb-auth", "/")
            }
        }

        verify(exactly = 1) { tokenService.generate(userEmail, roles.toList()) }

        confirmVerified(tokenService)
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "admin", roles = ["ADMINISTRATION"])
    fun `should logout and destroy the cookie with the token`() {
        mockMvc.post("$ENDPOINT_URL/logout") {
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
        }.andExpect {
            status { isOk() }
            cookie {
                exists("wb-auth")
                value("wb-auth", "")
                httpOnly("wb-auth", true)
                secure("wb-auth", true)
                maxAge("wb-auth", 0)
                path("wb-auth", "/")
            }
        }
    }

    @Test
    @WithMockUser(username = "user@test.com", password = "admin", roles = ["ADMINISTRATION"])
    fun `should return user information if authenticated`() {

        val expectedUser = createUser(name = "admin", email = "test@test.com")

        val jwt: Jwt = mockk()

        every { jwt.subject } returns expectedUser.email
        every { userRepository.findByEmail(ofType<String>()) } returns expectedUser

        val authorities = listOf(SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))
        val authentication = UsernamePasswordAuthenticationToken(jwt, null, authorities)

        val jsonResponse = mockMvc.get("$ENDPOINT_URL/me") {
            principal = authentication
            contentType = MediaType.APPLICATION_JSON
            with(csrf())
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse) {
            isObject
            node("name").isEqualTo("admin")
            node("email").isEqualTo("test@test.com")
        }

        verify(exactly = 1) { userRepository.findByEmail(ofType<String>()) }

        confirmVerified(userRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/auth"
    }
}