package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.TokenController
import com.nimbusds.jwt.JWTParser
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.post
import java.time.Instant

@TestPropertySource(
    properties = [
        "web-budget.jwt.access-token-expiration=1"
    ]
)
class TokenControllerTest : BaseControllerIntegrationTest() {

    @Test
    fun `should require basic authentication`() {
        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should generate the token`() {
        val responseBody = mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(httpBasic("admin@webbudget.com.br", "admin"))
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(responseBody) {
            isObject
            node("token").isNotNull
            node("email").isNotNull.isEqualTo("admin@webbudget.com.br")
            node("name").isNotNull.isEqualTo("Administrador")
        }
    }

    @Test
    fun `should generate token that will expire soon`() {
        val responseBody = mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(httpBasic("admin@webbudget.com.br", "admin"))
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        val tokenResponse = jsonToObject(responseBody, TokenController.TokenResponse::class.java)

        assertThat(tokenResponse)
            .isNotNull
            .hasFieldOrPropertyWithValue("name", "Administrador")
            .hasFieldOrPropertyWithValue("email", "admin@webbudget.com.br")
            .hasFieldOrProperty("token").isNotNull

        val expiration = JWTParser.parse(tokenResponse.token).jwtClaimsSet.expirationTime
        assertThat(expiration).isBefore(Instant.now())
    }

    companion object {
        private const val ENDPOINT_URL = "/token"
    }
}
