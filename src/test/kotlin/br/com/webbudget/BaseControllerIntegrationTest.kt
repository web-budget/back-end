package br.com.webbudget

import br.com.webbudget.utilities.ResourceAsStringResolver
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class, ResourceAsStringResolver::class)
abstract class BaseControllerIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Test
    fun `should require authorization`() {

        assumeTrue(enableAuthorizationTest(), "Authorization test not required")

        mockMvc.get(getEndpointUrl()) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    open fun enableAuthorizationTest(): Boolean {
        return true
    }

    abstract fun getEndpointUrl(): String
}
