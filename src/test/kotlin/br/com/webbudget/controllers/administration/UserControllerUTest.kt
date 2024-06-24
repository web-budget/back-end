package br.com.webbudget.controllers.administration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.administration.UserController
import br.com.webbudget.application.mappers.configuration.UserMapperImpl
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.administration.UserService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.ResourceAsString
import br.com.webbudget.utilities.fixture.createUser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.util.LinkedMultiValueMap
import java.util.UUID

@WebMvcTest(UserController::class)
@Import(value = [UserMapperImpl::class])
class UserControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var userService: UserService

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should call account creation and return created`(@ResourceAsString("user/create.json") payload: String) {

        val externalId = UUID.randomUUID()

        every { userService.createAccount(any(), any()) } returns externalId

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost$ENDPOINT_URL/$externalId")
            }
        }

        verify(exactly = 1) { userService.createAccount(any(), any()) }

        confirmVerified(userService)
    }

    @Test
    fun `should fail if required fields are not present`(@ResourceAsString("user/invalid.json") payload: String) {

        val requiredEntries = mapOf(
            "name" to "users.errors.name-is-blank",
            "email" to "users.errors.email-is-blank",
            "password" to "users.errors.password-is-blank",
            "authorities" to "users.errors.empty-authorities",
            "defaultLanguage" to "users.errors.null-default-language"
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .node("errors")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)

        verify { userService.createAccount(any(), any()) wasNot called }

        confirmVerified(userService)
    }

    @Test
    fun `should call account update and return ok`(@ResourceAsString("user/update.json") payload: String) {

        val authorities = listOf("FINANCIAL")
        val externalId = UUID.randomUUID()
        val expectedUser = createUser(externalId = externalId, authorities = authorities.toTypedArray())

        every { userRepository.findByExternalId(externalId) } returns expectedUser
        every { userService.updateAccount(expectedUser, authorities) } returns expectedUser

        val jsonResponse = mockMvc.put("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("name", expectedUser.name)
            .containsEntry("email", expectedUser.email)
            .containsEntry("active", expectedUser.active)
            .containsEntry("defaultLanguage", expectedUser.defaultLanguage.name)
            .containsEntry("id", expectedUser.externalId!!.toString())
            .node("authorities").isArray.contains("FINANCIAL")

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }
        verify(exactly = 1) { userService.updateAccount(expectedUser, authorities) }

        confirmVerified(userService, userRepository)
    }

    @Test
    fun `should call update for the password only`(@ResourceAsString("user/password-change.json") payload: String) {

        val password = "P4ssw0rd1"
        val externalId = UUID.randomUUID()
        val expectedUser = createUser(1L, externalId)

        every { userRepository.findByExternalId(externalId) } returns expectedUser
        every { userService.updatePassword(expectedUser, password, true) } just runs

        mockMvc.patch("$ENDPOINT_URL/$externalId/update-password") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload.replace("{new-password}", password)
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }
        verify(exactly = 1) { userService.updatePassword(expectedUser, password, true) }

        confirmVerified(userService, userRepository)
    }

    @Test
    fun `should get conflict if e-mail is duplicated`(@ResourceAsString("user/create.json") payload: String) {

        every { userService.createAccount(any(), any()) } throws
                DuplicatedPropertyException("users.errors.duplicated-email", "user.email")

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.error", equalTo("users.errors.duplicated-email"))
            jsonPath("\$.property", equalTo("user.email"))
        }

        verify(exactly = 1) { userService.createAccount(any(), any()) }

        confirmVerified(userService)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedUser = createUser(externalId = externalId, authorities = arrayOf("REGISTRATION"))

        every { userRepository.findByExternalId(externalId) } returns expectedUser

        val jsonResponse = mockMvc.get("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("name", expectedUser.name)
            .containsEntry("email", expectedUser.email)
            .containsEntry("active", expectedUser.active)
            .containsEntry("id", expectedUser.externalId!!.toString())
            .node("authorities").isArray.contains("REGISTRATION")

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }

        confirmVerified(userRepository)
    }

    @Test
    fun `should return not found if user does not exists`() {

        val externalId = UUID.randomUUID()

        every { userRepository.findByExternalId(externalId) } returns null

        mockMvc.get("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }

        confirmVerified(userRepository)
    }

    @Test
    fun `should call delete and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedUser = createUser(externalId = externalId, authorities = arrayOf("REGISTRATION"))

        every { userRepository.findByExternalId(externalId) } returns expectedUser
        every { userService.deleteAccount(expectedUser) } just runs

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }
        verify(exactly = 1) { userService.deleteAccount(expectedUser) }

        confirmVerified(userService, userRepository)
    }

    @Test
    fun `should call delete and get not found if no account is found`() {

        val externalId = UUID.randomUUID()

        every { userRepository.findByExternalId(externalId) } returns null

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }

        confirmVerified(userRepository)
    }

    @Test
    fun `should get bad request when try to delete admin user`() {

        val externalId = UUID.randomUUID()
        val adminUser = User(true, "Admin", "admin@webbudget.com.br", "s3cr3t", PT_BR)
            .apply {
                this.externalId = externalId
            }

        every { userRepository.findByExternalId(externalId) } returns adminUser
        every { userService.deleteAccount(adminUser) } throws IllegalArgumentException("Can't delete admin")

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 1) { userRepository.findByExternalId(externalId) }
        verify(exactly = 1) { userService.deleteAccount(adminUser) }

        confirmVerified(userRepository)
    }

    @Test
    fun `should call get paged and using filters`() {

        val pageRequest = PageRequest.of(0, 1)
        val users = listOf(createUser(1L, UUID.randomUUID()))

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", pageRequest.pageNumber.toString())
        parameters.add("size", pageRequest.pageSize.toString())

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Some filter")

        val pageableSlot = slot<Pageable>()
        val specificationSlot = slot<Specification<User>>()

        val thePage = PageImpl(users, pageRequest, users.size.toLong())

        every { userRepository.findAll(capture(specificationSlot), capture(pageableSlot)) } returns thePage

        val jsonResponse = mockMvc.get(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.ADMINISTRATION))
            contentType = MediaType.APPLICATION_JSON
            params = parameters
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("totalElements", 1)
            .containsEntry("totalPages", 1)
            .containsEntry("size", pageRequest.pageSize)
            .containsEntry("number", pageRequest.pageNumber)
            .containsEntry("empty", false)

        assertThatJson(jsonResponse)
            .node("content")
            .isArray
            .isNotEmpty

        assertThat(pageableSlot.captured)
            .isNotNull
            .satisfies({
                assertThat(it.pageNumber).isEqualTo(pageRequest.pageNumber)
                assertThat(it.pageSize).isEqualTo(pageRequest.pageSize)
            })

        assertThat(specificationSlot.captured).isNotNull

        verify(exactly = 1) { userRepository.findAll(ofType<Specification<User>>(), ofType<Pageable>()) }

        confirmVerified(userRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/administration/users"
    }
}
