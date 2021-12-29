package br.com.webbudget.controllers.administration

import br.com.webbudget.ControllerTestRunner
import br.com.webbudget.application.payloads.UserForm
import br.com.webbudget.application.payloads.UserView
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.util.LinkedMultiValueMap
import java.util.UUID

class UserControllerTest : ControllerTestRunner() {

    @Value("classpath:/payloads/user/create-user.json")
    private lateinit var createUserJson: Resource

    @Value("classpath:/payloads/user/update-user.json")
    private lateinit var updateUserJson: Resource

    @Value("classpath:/payloads/user/invalid-user.json")
    private lateinit var invalidUserJson: Resource

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
    @WithMockUser
    fun `should fail if required fields are not present`() {

        val payload = resourceAsString(invalidUserJson)
        val requiredFields = arrayOf("name", "email", "password", "authorities")

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andExpect {
            jsonPath("\$.violations[*].property", containsInAnyOrder(*requiredFields))
        }
    }

    @Test
    @WithMockUser
    fun `should update an user account`() {

        val payload = resourceAsString(updateUserJson)

        mockMvc.put("$ENDPOINT_URL/e443f25b-2a6f-4a7a-8ecd-054dfba8fd19") {
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
    @WithMockUser
    fun `should update only password`() {

        val payload = "testing"

        mockMvc.patch("$ENDPOINT_URL/6706a395-6690-4bad-948a-5c3c823e93d2/update-password") {
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
    @WithMockUser
    fun `should get conflict if e-mail is duplicated`() {

        val payload = resourceAsString(createUserJson)
            .replace("user@webbudget.com.br", "admin@webbudget.com.br")

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.property", equalTo("user.email"))
        }
    }

    @Test
    @WithMockUser
    fun `should find an user by id`() {

        val userId = UUID.fromString("6706a395-6690-4bad-948a-5c3c823e93d2")

        val result = mockMvc.get("$ENDPOINT_URL/$userId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val found = jsonToObject(result.response.contentAsString, UserForm::class.java)

        assertThat(found).isNotNull
        assertThat(found.id).isEqualTo(userId)
        assertThat(found.authorities).containsExactlyInAnyOrder(
            "DASHBOARDS",
            "REGISTRATION",
            "FINANCIAL",
            "ADMINISTRATION"
        )
    }

    @Test
    @WithMockUser
    fun `should get no content if user does not exists`() {

        val userId = UUID.randomUUID()

        mockMvc.get("$ENDPOINT_URL/$userId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }

        val found = userRepository.findByExternalId(userId)
        assertThat(found).isNull()
    }

    @Test
    @WithMockUser
    fun `should delete an user account`() {

        mockMvc.delete("$ENDPOINT_URL/f4032b91-c4ff-4a4c-bf9e-43b28c909e1d") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        val found = userRepository.findByExternalId(UUID.fromString("f4032b91-c4ff-4a4c-bf9e-43b28c909e1d"))
        assertThat(found).isNull()
    }

    @Test
    @WithMockUser
    fun `should get no content when deleting an unknown user account`() {

        val userId = UUID.randomUUID()

        mockMvc.delete("$ENDPOINT_URL/$userId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }

        val found = userRepository.findByExternalId(userId)
        assertThat(found).isNull()
    }

    @Test
    @WithMockUser
    fun `should find users using filters`() {

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", "0")
        parameters.add("size", "1")
        parameters.add("active", "true")

        parameters.add("state", "ACTIVE")
        parameters.add("filter", "Administrador")

        val result = mockMvc.get(ENDPOINT_URL) {
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
        private const val ENDPOINT_URL = "/api/users/"
    }
}
