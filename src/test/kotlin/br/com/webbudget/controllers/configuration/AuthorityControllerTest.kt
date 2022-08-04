package br.com.webbudget.controllers.configuration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.infrastructure.repository.configuration.AuthorityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get

class AuthorityControllerTest : BaseControllerIntegrationTest() {

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Test
    @WithMockUser
    fun `should get all authorities from database in a string list`() {

        val result = mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
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
        private const val ENDPOINT_URL = "/api/authorities"
    }
}
