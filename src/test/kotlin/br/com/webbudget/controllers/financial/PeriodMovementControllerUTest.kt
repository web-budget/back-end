package br.com.webbudget.controllers.financial

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.financial.PeriodMovementController
import br.com.webbudget.application.mappers.financial.ApportionmentMapperImpl
import br.com.webbudget.application.mappers.financial.PeriodMovementMapperImpl
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.application.payloads.ErrorCodes.IS_EMPTY
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.financial.PeriodMovementFilter
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.services.financial.PeriodMovementService
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.JsonPayload
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import br.com.webbudget.utilities.fixture.createMovementClass
import br.com.webbudget.utilities.fixture.createPeriodMovement
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

@WebMvcTest(PeriodMovementController::class)
@Import(
    value = [
        PeriodMovementMapperImpl::class,
        ApportionmentMapperImpl::class,
        CostCenterMapperImpl::class,
        FinancialPeriodMapperImpl::class,
        MovementClassMapperImpl::class
    ]
)
class PeriodMovementControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    @Suppress("UnusedPrivateMember")
    private lateinit var costCenterRepository: CostCenterRepository

    @MockkBean
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @MockkBean
    private lateinit var movementClassRepository: MovementClassRepository

    @MockkBean
    private lateinit var periodMovementRepository: PeriodMovementRepository

    @MockkBean
    private lateinit var periodMovementService: PeriodMovementService

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
        val financialPeriodId = UUID.fromString("bc67ba91-7c0a-466d-877c-0b1fe2fb56bd")
        val movementClassId = UUID.fromString("ff8ac873-2cbd-43dd-a3e8-2bc451f4e3fa")

        val movementClass = createMovementClass()
        val financialPeriod = createFinancialPeriod()

        every { periodMovementService.create(any<PeriodMovement>()) } returns externalId
        every { movementClassRepository.findByExternalId(eq(movementClassId)) } returns movementClass
        every { financialPeriodRepository.findByExternalId(eq(financialPeriodId)) } returns financialPeriod

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("period-movement/create")
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost$ENDPOINT_URL/$externalId")
            }
        }

        verify(exactly = 1) { periodMovementService.create(ofType<PeriodMovement>()) }
        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(periodMovementService, movementClassRepository, financialPeriodRepository)
    }

    @Test
    fun `should update period movement and return ok`() {

        val externalId = UUID.randomUUID()
        val financialPeriodId = UUID.fromString("bc67ba91-7c0a-466d-877c-0b1fe2fb56bd")
        val movementClassId = UUID.fromString("ff8ac873-2cbd-43dd-a3e8-2bc451f4e3fa")

        val periodMovement = createPeriodMovement()
        val movementClass = createMovementClass()
        val financialPeriod = createFinancialPeriod()

        every { periodMovementService.update(any<PeriodMovement>()) } returns periodMovement

        every { periodMovementRepository.findByExternalId(eq(externalId)) } returns periodMovement
        every { movementClassRepository.findByExternalId(eq(movementClassId)) } returns movementClass
        every { financialPeriodRepository.findByExternalId(eq(financialPeriodId)) } returns financialPeriod

        mockMvc.put("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("period-movement/update")
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { periodMovementService.update(ofType<PeriodMovement>()) }
        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { periodMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(
            periodMovementService,
            movementClassRepository,
            financialPeriodRepository,
            periodMovementRepository
        )
    }

    @Test
    fun `should delete period movement and return ok`() {

        val externalId = UUID.randomUUID()

        val periodMovement = createPeriodMovement()

        every { periodMovementService.delete(any<PeriodMovement>()) } just runs
        every { periodMovementRepository.findByExternalId(eq(externalId)) } returns periodMovement

        mockMvc.delete("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { periodMovementService.delete(ofType<PeriodMovement>()) }
        verify(exactly = 1) { periodMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(periodMovementService, periodMovementRepository)
    }

    @Test
    fun `should expect not found if try to delete something that does not exist`() {

        val externalId = UUID.randomUUID()

        every { periodMovementRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.delete("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 0) { periodMovementService.delete(ofType<PeriodMovement>()) }
        verify(exactly = 1) { periodMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(periodMovementService, periodMovementRepository)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present when creating`() {

        val requiredEntries = mapOf(
            "name" to IS_NULL_OR_BLANK,
            "dueDate" to IS_NULL,
            "value" to IS_NULL,
            "financialPeriod" to IS_NULL,
            "apportionments" to IS_EMPTY
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("period-movement/unprocessable")
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .node("violations")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present when updating`() {

        val requiredEntries = mapOf(
            "name" to IS_NULL_OR_BLANK,
            "dueDate" to IS_NULL,
            "value" to IS_NULL,
            "financialPeriod" to IS_NULL,
            "apportionments" to IS_EMPTY
        )

        val jsonResponse = mockMvc.put("$ENDPOINT_URL/{id}", UUID.randomUUID()) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("period-movement/unprocessable")
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .node("violations")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)
    }

    @Test
    fun `should expect bad request if validations failed`() {

        val financialPeriodId = UUID.fromString("bc67ba91-7c0a-466d-877c-0b1fe2fb56bd")
        val movementClassId = UUID.fromString("ff8ac873-2cbd-43dd-a3e8-2bc451f4e3fa")

        val movementClass = createMovementClass()
        val financialPeriod = createFinancialPeriod()

        every { periodMovementService.create(any<PeriodMovement>()) } throws BusinessException("Message", "Detail")
        every { movementClassRepository.findByExternalId(eq(movementClassId)) } returns movementClass
        every { financialPeriodRepository.findByExternalId(eq(financialPeriodId)) } returns financialPeriod

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("period-movement/create")
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 1) { periodMovementService.create(ofType<PeriodMovement>()) }
        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { financialPeriodRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(periodMovementService, movementClassRepository, financialPeriodRepository)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()

        val periodMovement = createPeriodMovement()

        every { periodMovementRepository.findByExternalId(eq(externalId)) } returns periodMovement

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

        verify(exactly = 1) { periodMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(periodMovementRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { periodMovementRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.get("$ENDPOINT_URL/{id}", externalId) {
            with(jwt().authorities(Authorities.FINANCIAL))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { periodMovementRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(periodMovementRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val periodMovements = listOf(createPeriodMovement(), createPeriodMovement())
        val page = PageImpl(periodMovements, PageRequest.of(0, 1), periodMovements.size.toLong())

        every { periodMovementRepository.findByFilter(any<PeriodMovementFilter>(), any<Pageable>()) } returns page

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

        verify(exactly = 1) { periodMovementRepository.findByFilter(any<PeriodMovementFilter>(), any<Pageable>()) }

        confirmVerified(periodMovementRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/financial/period-movements"
    }
}