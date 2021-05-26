package br.com.webbudget.controllers.administration

import br.com.webbudget.AbstractControllerTest
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class UserControllerTest : AbstractControllerTest() {

    @Value("classpath:/payloads/user/create-user.json")
    private lateinit var createUserJson: Resource

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
    @WithMockUser
    fun `should create an user account`() {

        val payload = resourceAsString(createUserJson)

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isCreated() }
        }

        val user = userRepository.findByEmail("test@webbudget.com.br")
        assertThat(user).isNotNull

        user?.let {
            assertThat(it.name).isEqualTo("Test User")
            assertThat(it.email).isEqualTo("test@webbudget.com.br")

            assertThat(passwordEncoder.matches("testing", it.password)).isTrue

            assertThat(it.id).isNotNull
            assertThat(it.externalId).isNotNull

            assertThat(it.grants).isNotEmpty

            val roles = it.grants!!
                .map { grant -> grant.authority.name }
                .toCollection(mutableListOf())

            assertThat(roles).containsExactlyInAnyOrder("ADMINISTRATION", "REGISTRATION")
        }
    }

    companion object {
        private const val ENDPOINT_URL = "/api/users/"
    }
}
