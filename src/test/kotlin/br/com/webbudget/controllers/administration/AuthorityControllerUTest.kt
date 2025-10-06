package br.com.webbudget.controllers.administration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.administration.RoleController
import br.com.webbudget.infrastructure.repository.administration.RoleRepository
import br.com.webbudget.utilities.Roles
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get

@WebMvcTest(RoleController::class)
class AuthorityControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var roleRepository: RoleRepository

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    @WithMockUser(roles = [Roles.ADMINISTRATION])
    fun `should get all authorities`() {

        every { roleRepository.findAll() } returns Roles.asList()

        val jsonResponse = mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isArray
            .isNotEmpty
            .containsExactlyInAnyOrder("ADMINISTRATION", "REGISTRATION", "FINANCIAL", "DASHBOARDS")

        verify(exactly = 1) { roleRepository.findAll() }

        confirmVerified(roleRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/administration/roles"
    }
}
