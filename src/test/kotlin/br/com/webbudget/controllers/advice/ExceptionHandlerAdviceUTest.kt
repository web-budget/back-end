package br.com.webbudget.controllers.advice

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.utilities.Roles
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get

@WithMockUser(roles = [Roles.ADMINISTRATION])
@WebMvcTest(GeneralExceptionController::class)
class ExceptionHandlerAdviceUTest : BaseControllerIntegrationTest() {

    @Test
    fun `should get unauthorized when bad credentials exception`() {
        mockMvc.get("$ENDPOINT_URL/bad-credentials-exception") {
            content = MediaType.APPLICATION_JSON_VALUE
        }.andExpect {
            status { isUnauthorized() }
        }.andReturn()
            .response
            .contentAsString
    }

    @Test
    fun `should get bad request when illegal argument exception`() {

        val response = mockMvc.get("$ENDPOINT_URL/illegal-argument-exception") {
            content = MediaType.APPLICATION_JSON_VALUE
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .isObject
            .containsEntry("detail", "Illegal argument")
            .containsEntry("title", "Bad Request")
            .containsEntry("status", 400)
    }

    @Test
    fun `should get bad request when non transient data exception`() {

        val response = mockMvc.get("$ENDPOINT_URL/non-transient-data-access-exception") {
            content = MediaType.APPLICATION_JSON_VALUE
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .isObject
            .containsEntry("detail", "Data integrity violation")
            .containsEntry("title", "Bad Request")
            .containsEntry("status", 400)
    }

    @Test
    fun `should get bad request when business exception`() {

        val response = mockMvc.get("$ENDPOINT_URL/business-exception") {
            content = MediaType.APPLICATION_JSON_VALUE
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .isObject
            .containsEntry("detail", "The message")
            .containsEntry("title", "Bad Request")
            .containsEntry("status", 400)
    }

    companion object {
        private const val ENDPOINT_URL = "/general-exceptions"
    }
}
