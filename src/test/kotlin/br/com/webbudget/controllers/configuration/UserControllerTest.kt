package br.com.webbudget.controllers.configuration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.payloads.configuration.UserForm
import br.com.webbudget.application.payloads.configuration.UserView
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.ResourceAsString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.util.LinkedMultiValueMap
import java.util.UUID

class UserControllerTest : BaseControllerIntegrationTest() { // TODO refactor

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `should require authentication`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should create an user account`(@ResourceAsString("user/create.json") payload: String) {

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isCreated() }
        }

        val user = userRepository.findByEmail("user@webbudget.com.br")
        assertThat(user).isNotNull

        user?.let {
            assertThat(it.name).isEqualTo("User")
            assertThat(it.email).isEqualTo("user@webbudget.com.br")

            assertThat(passwordEncoder.matches("user", it.password)).isTrue

            assertThat(it.id).isNotNull
            assertThat(it.externalId).isNotNull
            assertThat(it.active).isFalse

            assertThat(it.grants).isNotEmpty

            val authorities = it.grants!!
                .map { grant -> grant.authority.name }
                .toCollection(mutableListOf())

            assertThat(authorities).containsExactlyInAnyOrder("REGISTRATION")
        }
    }

    @Test
    fun `should fail if required fields are not present`(@ResourceAsString("user/invalid.json") payload: String) {

        val requiredFields = arrayOf("name", "email", "password", "authorities")

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andExpect {
            jsonPath("\$.violations[*].property", containsInAnyOrder(*requiredFields))
        }
    }

    @Test
    fun `should update an user account`(@ResourceAsString("user/update.json") payload: String) {

        mockMvc.put("$ENDPOINT_URL/e443f25b-2a6f-4a7a-8ecd-054dfba8fd19") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isOk() }
        }

        val user = userRepository.findByEmail("update@webbudget.com.br")
        assertThat(user).isNotNull

        user?.let {
            assertThat(it.name).isEqualTo("Updated User")
            assertThat(it.email).isEqualTo("update@webbudget.com.br")

            assertThat(passwordEncoder.matches("user", it.password)).isTrue

            assertThat(it.externalId).isEqualTo(UUID.fromString("e443f25b-2a6f-4a7a-8ecd-054dfba8fd19"))
            assertThat(it.active).isTrue

            assertThat(it.grants).isNotEmpty

            val authorities = it.grants!!
                .map { grant -> grant.authority.name }
                .toCollection(mutableListOf())

            assertThat(authorities).containsExactlyInAnyOrder("FINANCIAL")
        }
    }

    @Test
    fun `should update only password`() {

        val payload = "testing"

        mockMvc.patch("$ENDPOINT_URL/6706a395-6690-4bad-948a-5c3c823e93d2/update-password") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isOk() }
        }

        val user = userRepository.findByEmail("admin@webbudget.com.br")
        assertThat(user).isNotNull

        user?.let {
            assertThat(it.name).isEqualTo("Administrador")
            assertThat(it.email).isEqualTo("admin@webbudget.com.br")

            assertThat(passwordEncoder.matches("testing", it.password)).isTrue

            assertThat(it.externalId).isEqualTo(UUID.fromString("6706a395-6690-4bad-948a-5c3c823e93d2"))
            assertThat(it.active).isTrue

            assertThat(it.grants).isNotEmpty

            val authorities = it.grants!!
                .map { grant -> grant.authority.name }
                .toCollection(mutableListOf())

            assertThat(authorities).containsExactlyInAnyOrder(
                "DASHBOARDS",
                "REGISTRATION",
                "FINANCIAL",
                "ADMINISTRATION"
            )
        }
    }

    @Test
    fun `should get conflict if e-mail is duplicated`(@ResourceAsString("user/create.json") payload: String) {

        payload.replace("user@webbudget.com.br", "admin@webbudget.com.br")

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.property", equalTo("user.email"))
        }
    }

    @Test
    fun `should find an user by id`() {

        val userId = UUID.fromString("6706a395-6690-4bad-948a-5c3c823e93d2")

        val result = mockMvc.get("$ENDPOINT_URL/$userId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val found = jsonToObject(result.response.contentAsString, UserForm::class.java)

        assertThat(found).isNotNull
        assertThat(found.authorities).containsExactlyInAnyOrder(
            "DASHBOARDS",
            "REGISTRATION",
            "FINANCIAL",
            "ADMINISTRATION"
        )
    }

    @Test
    fun `should get no content if user does not exists`() {

        val userId = UUID.randomUUID()

        mockMvc.get("$ENDPOINT_URL/$userId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }

        val found = userRepository.findByExternalId(userId)
        assertThat(found).isNull()
    }

    @Test
    fun `should delete an user account`() {

        mockMvc.delete("$ENDPOINT_URL/f4032b91-c4ff-4a4c-bf9e-43b28c909e1d") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        val found = userRepository.findByExternalId(UUID.fromString("f4032b91-c4ff-4a4c-bf9e-43b28c909e1d"))
        assertThat(found).isNull()
    }

    @Test
    fun `should get no content when deleting an unknown user account`() {

        val userId = UUID.randomUUID()

        mockMvc.delete("$ENDPOINT_URL/$userId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }

        val found = userRepository.findByExternalId(userId)
        assertThat(found).isNull()
    }

    @Test
    fun `should find users using filters`() {

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", "0")
        parameters.add("size", "1")
        parameters.add("active", "true")

        parameters.add("state", "ACTIVE")
        parameters.add("filter", "Administrador")

        val result = mockMvc.get(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            params = parameters
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val users = jsonToObject(result.response.contentAsString, "/content", UserView::class.java)

        assertThat(users)
            .hasSize(1)
            .extracting("id", "name", "email")
            .contains(tuple("6706a395-6690-4bad-948a-5c3c823e93d2", "Administrador", "admin@webbudget.com.br"))
    }

    companion object {
        private const val ENDPOINT_URL = "/api/administration/users"
    }
}
