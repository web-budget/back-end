package br.com.webbudget.controllers.advice

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.utilities.Authorities
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.get

@WebMvcTest(ValidationExceptionController::class)
class ValidationHandlerAdviceUTest : BaseControllerIntegrationTest() {

    @Test
    fun `should get conflict when duplicated property`() {

        val response = mockMvc.get("$ENDPOINT_URL/duplicated-property-exception") {
            content = MediaType.APPLICATION_JSON_VALUE
            with(jwt().authorities(Authorities.ADMINISTRATION))
        }.andExpect {
            status { isConflict() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .isObject
            .containsEntry("detail", "The message")
            .containsEntry("title", "Conflict")
            .containsEntry("status", 409)
            .node("conflicts")
            .isObject
            .containsEntry("property", "value")
    }

    @Test
    fun `should get unprocessable entity when method argument exception`() {

        val response = mockMvc.get("$ENDPOINT_URL/method-argument-no-valid-exception") {
            content = MediaType.APPLICATION_JSON_VALUE
            with(jwt().authorities(Authorities.ADMINISTRATION))
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .isObject
            .containsEntry("detail", "Some fields are missing or invalid")
            .containsEntry("title", "Unprocessable Entity")
            .containsEntry("status", 422)
    }

    companion object {
        private const val ENDPOINT_URL = "/validation-exceptions"
    }
}