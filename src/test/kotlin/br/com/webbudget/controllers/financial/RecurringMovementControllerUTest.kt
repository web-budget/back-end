package br.com.webbudget.controllers.financial

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.financial.RecurringMovementController
import br.com.webbudget.application.mappers.financial.ApportionmentMapperImpl
import br.com.webbudget.application.mappers.financial.RecurringMovementMapperImpl
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.application.payloads.ErrorCodes.IS_EMPTY
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.financial.RecurringMovementFilter
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.services.financial.RecurringMovementService
import br.com.webbudget.infrastructure.repository.financial.RecurringMovementRepository
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.JsonPayload
import br.com.webbudget.utilities.fixture.createMovementClass
import br.com.webbudget.utilities.fixture.createRecurringMovement
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.UUID

@WebMvcTest(RecurringMovementController::class)
@Import(
    value = [
        RecurringMovementMapperImpl::class,
        ApportionmentMapperImpl::class,
        CostCenterMapperImpl::class,
        MovementClassMapperImpl::class
    ]
)
class RecurringMovementControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    @Suppress("UnusedPrivateMember")
    private lateinit var costCenterRepository: CostCenterRepository

    @MockkBean
    private lateinit var movementClassRepository: MovementClassRepository

    @MockkBean
    private lateinit var recurringMovementRepository: RecurringMovementRepository

    @MockkBean
    private lateinit var recurringMovementService: RecurringMovementService

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should create period movement and return created`() {

        val externalId = UUID.randomUUID()
        val movementClassId = UUID.fromString("0100a0f9-ccbb-4f61-a6fa-fb4644361ffd")

        val movementClass = createMovementClass()

        every { recurringMovementService.create(any<RecurringMovement>()) } returns externalId
        every { movementClassRepository.findByExternalId(eq(movementClassId)) } returns movementClass

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("recurring-movement/create")
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost$ENDPOINT_URL/$externalId")
            }
        }

        verify(exactly = 1) { recurringMovementService.create(ofType<RecurringMovement>()) }
        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(recurringMovementService, movementClassRepository)
    }

    @Test
    fun `should update period movement and return ok`() {

        val externalId = UUID.randomUUID()
        val movementClassId = UUID.fromString("0100a0f9-ccbb-4f61-a6fa-fb4644361ffd")

        val recurringMovement = createRecurringMovement()
        val movementClass = createMovementClass()

        every { recurringMovementService.update(any<RecurringMovement>()) } returns recurringMovement

        every { recurringMovementRepository.findByExternalId(eq(externalId)) } returns recurringMovement
        every { movementClassRepository.findByExternalId(eq(movementClassId)) } returns movementClass

        mockMvc.put("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("recurring-movement/update")
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { recurringMovementService.update(ofType<RecurringMovement>()) }
        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { recurringMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(
            recurringMovementService,
            movementClassRepository,
            recurringMovementRepository
        )
    }

    @Test
    fun `should delete period movement and return ok`() {

        val externalId = UUID.randomUUID()

        val recurringMovement = createRecurringMovement()

        every { recurringMovementService.delete(any<RecurringMovement>()) } just runs
        every { recurringMovementRepository.findByExternalId(eq(externalId)) } returns recurringMovement

        mockMvc.delete("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { recurringMovementService.delete(ofType<RecurringMovement>()) }
        verify(exactly = 1) { recurringMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(recurringMovementService, recurringMovementRepository)
    }

    @Test
    fun `should expect not found if try to delete something that does not exist`() {

        val externalId = UUID.randomUUID()

        every { recurringMovementRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.delete("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 0) { recurringMovementService.delete(ofType<RecurringMovement>()) }
        verify(exactly = 1) { recurringMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(recurringMovementService, recurringMovementRepository)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present when creating`() {

        val requiredEntries = mapOf(
            "name" to IS_NULL_OR_BLANK,
            "startingAt" to IS_NULL,
            "value" to IS_NULL,
            "apportionments" to IS_EMPTY,
            "autoLaunch" to IS_NULL
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("recurring-movement/unprocessable")
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .node("message")
            .isObject
            .containsKey("key")
            .node("parameters")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present when updating`() {

        val requiredEntries = mapOf(
            "name" to IS_NULL_OR_BLANK,
            "startingAt" to IS_NULL,
            "apportionments" to IS_EMPTY,
            "autoLaunch" to IS_NULL
        )

        val jsonResponse = mockMvc.put("$ENDPOINT_URL/{id}", UUID.randomUUID()) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("recurring-movement/unprocessable")
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .node("message")
            .isObject
            .containsKey("key")
            .node("parameters")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)
    }

    @Test
    fun `should expect bad request if validations failed`() {

        val movementClassId = UUID.fromString("0100a0f9-ccbb-4f61-a6fa-fb4644361ffd")

        val movementClass = createMovementClass()

        every { recurringMovementService.create(any<RecurringMovement>()) } throws BusinessException(
            "Message",
            "Detail"
        )
        every { movementClassRepository.findByExternalId(eq(movementClassId)) } returns movementClass

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("recurring-movement/create")
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 1) { recurringMovementService.create(ofType<RecurringMovement>()) }
        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(recurringMovementService, movementClassRepository)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()

        val recurringMovement = createRecurringMovement()

        every { recurringMovementRepository.findByExternalId(eq(externalId)) } returns recurringMovement

        val jsonResponse = mockMvc.get("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .node("apportionments")
            .isArray
            .isNotEmpty

        verify(exactly = 1) { recurringMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(recurringMovementRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { recurringMovementRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.get("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { recurringMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(recurringMovementRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val recurringMovements = listOf(createRecurringMovement(), createRecurringMovement())
        val page = PageImpl(recurringMovements, PageRequest.of(0, 1), recurringMovements.size.toLong())

        every { recurringMovementRepository.findByFilter(any<RecurringMovementFilter>(), any<Pageable>()) } returns page

        val jsonResponse = mockMvc.get(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .node("content")
            .isArray
            .isNotEmpty
            .hasSize(2)

        verify(exactly = 1) {
            recurringMovementRepository.findByFilter(
                any<RecurringMovementFilter>(),
                any<Pageable>()
            )
        }

        confirmVerified(recurringMovementRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/financial/recurring-movements"
    }
}