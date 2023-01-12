package br.com.webbudget.controllers.administration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.infrastructure.repository.administration.AuthorityRepository
import br.com.webbudget.utilities.Authorities
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.get

class AuthorityControllerTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var authorityRepository: AuthorityRepository

    @Test
    fun `should require authentication`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should get all authorities`() {

        every { authorityRepository.findAll() } returns Authorities.asList()

        val jsonResponse = mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(jwt().authorities(Authorities.ADMINISTRATION))
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isArray
            .isNotEmpty
            .containsExactlyInAnyOrder("ADMINISTRATION", "REGISTRATION", "FINANCIAL", "DASHBOARDS")

        verify(exactly = 1) { authorityRepository.findAll() }

        confirmVerified(authorityRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/administration/authorities"
    }
}
