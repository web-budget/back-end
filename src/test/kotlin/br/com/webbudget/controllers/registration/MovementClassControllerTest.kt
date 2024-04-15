package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.MovementClassController
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.MovementClassService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.ResourceAsString
import br.com.webbudget.utilities.fixture.createCostCenter
import br.com.webbudget.utilities.fixture.createMovementClass
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(MovementClassController::class)
@Import(value = [MovementClassMapperImpl::class, CostCenterMapperImpl::class])
class MovementClassControllerTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var movementClassService: MovementClassService

    @MockkBean
    private lateinit var movementClassRepository: MovementClassRepository

    @MockkBean
    private lateinit var costCenterRepository: CostCenterRepository

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should call create and expect created`(@ResourceAsString("movement-class/create.json") payload: String) {

        val externalId = UUID.randomUUID()

        every { movementClassService.create(any<MovementClass>()) } returns externalId
        every { costCenterRepository.findByExternalId(any<UUID>()) } returns createCostCenter()

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost${ENDPOINT_URL}/$externalId")
            }
        }

        verify(exactly = 1) { movementClassService.create(ofType<MovementClass>()) }
        verify(exactly = 1) { costCenterRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassService, costCenterRepository)
    }

    @Test
    fun `should call update and expect ok`(@ResourceAsString("wallet/update.json") payload: String) {

    }

    @Test
    fun `should call delete and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedMovementClass = createMovementClass(externalId = externalId)

        every { movementClassRepository.findByExternalId(eq(externalId)) } returns expectedMovementClass
        every { movementClassService.delete(eq(expectedMovementClass)) } just Runs

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { movementClassRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 1) { movementClassService.delete(eq(expectedMovementClass)) }

        confirmVerified(movementClassService, movementClassRepository)
    }

    @Test
    fun `should expect not found if try to delete something that does not exist`() {

        val externalId = UUID.randomUUID()

        every { movementClassRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { movementClassRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 0) { movementClassService.delete(ofType<MovementClass>()) }

        confirmVerified(movementClassService, movementClassRepository)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present`(
        @ResourceAsString("movement-class/invalid.json") payload: String
    ) {

    }

    @Test
    fun `should expect conflict if name is duplicated`(
        @ResourceAsString("movement-class/create.json") payload: String
    ) {
        val exception = DuplicatedPropertyException("movement-class.errors.duplicated-name", "movement-class.name")

        every { costCenterRepository.findByExternalId(any<UUID>()) } returns createCostCenter()
        every { movementClassService.create(any<MovementClass>()) } throws exception

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.property", equalTo("movement-class.name"))
            jsonPath("\$.error", equalTo("movement-class.errors.duplicated-name"))
        }

        verify(exactly = 1) { movementClassService.create(ofType<MovementClass>()) }
        verify(exactly = 1) { costCenterRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassService, costCenterRepository)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedMovementClass = createMovementClass(externalId = externalId)

        every { movementClassRepository.findByExternalId(externalId) } returns expectedMovementClass

        val jsonResponse = mockMvc.get("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", externalId.toString())
            .containsEntry("name", "Movement Class")
            .containsEntry("type", "INCOME")
            .containsEntry("description", "Some description")
            .containsEntry("active", true)

        assertThatJson(jsonResponse)
            .node("costCenter")
            .isObject
            .isNotNull

        verify(exactly = 1) { movementClassRepository.findByExternalId(eq(externalId)) }

        confirmVerified(movementClassRepository)

    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

    }

    @Test
    fun `should call get and expect paged result`() {

    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/movement-classes"
    }
}