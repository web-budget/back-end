package br.com.webbudget.controllers.administration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.infrastructure.repository.administration.AuthorityRepository
import br.com.webbudget.utilities.Authorities
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.get

class AuthorityControllerTest : BaseControllerIntegrationTest() {

    @Autowired
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
    fun `should find all authorities`() {

        val result = mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            with(jwt().authorities(Authorities.ADMINISTRATION))
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val foundAuthorities = jsonToObject(result.response.contentAsString, List::class.java)
        assertThat(foundAuthorities).isNotEmpty

        val databaseAuthorities = authorityRepository.findAll()
        assertThat(databaseAuthorities).isNotEmpty

        assertThat(databaseAuthorities)
            .extracting("name")
            .containsExactlyInAnyOrderElementsOf(foundAuthorities)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/administration/authorities"
    }
}
