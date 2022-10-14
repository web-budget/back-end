package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.post


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
        }
    }

    companion object {
        private const val ENDPOINT_URL = "/token"
    }
}
