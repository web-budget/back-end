package br.com.webbudget.controllers.administration

import br.com.webbudget.AbstractControllerTest
import br.com.webbudget.application.payloads.UserRequest
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class UserControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `should require proper authentication`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should create an user account`() {

        val payload = UserRequest("Test User", "test@webbudget.com.br", "password")

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectToJson(payload)
        }.andExpect {
            status { isCreated() }
        }

        val user = userRepository.findByEmail(payload.email)
        assertThat(user).isNotNull

        user?.let {
            assertThat(it.name).isEqualTo(payload.name)
            assertThat(it.email).isEqualTo(payload.email)

            assertThat(passwordEncoder.matches(payload.password, it.password)).isTrue

            assertThat(it.id).isNotNull
            assertThat(it.externalId).isNotNull
            assertThat(it.grants).isNotEmpty
        }
    }

    companion object {
        private const val ENDPOINT_URL = "/api/users/"
    }
}
