package br.com.webbudget.backend.controllers

import br.com.webbudget.backend.AbstractControllerTest
import br.com.webbudget.backend.application.payloads.Credential
import br.com.webbudget.backend.application.payloads.RefreshCredential
import br.com.webbudget.backend.application.payloads.Token
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class AuthenticationControllerTest : AbstractControllerTest() {

    @Value("\${web-budget.jwt.access-token-expiration}")
    private var secondsToExpireToken: Int? = 0

    @Test
    fun `should login and receive valid token`() {
        mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            content = toJson(Credential("admin@webbudget.com.br", "admin"))
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.accessToken", notNullValue())
            jsonPath("$.refreshToken", notNullValue())
            jsonPath("$.expireIn", `is`(secondsToExpireToken))
        }
    }

    @Test
    fun `should be unauthorized when bad credentials`() {
        mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            content = toJson(Credential("baduser@webbudget.com.br", "admin"))
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should refresh token`() {

        val oldTokenJson = mockMvc.post("$ENDPOINT_URL/login") {
            contentType = MediaType.APPLICATION_JSON
            content = toJson(Credential("admin@webbudget.com.br", "admin"))
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.accessToken", notNullValue())
            jsonPath("$.refreshToken", notNullValue())
        }.andReturn()
            .response
            .contentAsString

        val oldToken = fromJson(oldTokenJson, Token::class.java)
        val refreshCredential = RefreshCredential("admin@webbudget.com.br", oldToken.refreshToken)

        Thread.sleep(5000) // sleep to let jwt have different issue dates

        val newTokenJson = mockMvc.post("$ENDPOINT_URL/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = toJson(refreshCredential)
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("$.accessToken", notNullValue())
            jsonPath("$.refreshToken", notNullValue())
        }.andReturn()
            .response
            .contentAsString

        val newToken = fromJson(newTokenJson, Token::class.java)
        assertThat(newToken.accessToken).isNotEqualTo(oldToken.accessToken)
        assertThat(newToken.refreshToken.toString()).isNotEqualTo(oldToken.refreshToken.toString())
    }

    companion object {
        private const val ENDPOINT_URL = "/authentication/"
    }
}