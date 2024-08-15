package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.FinancialPeriodController
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapperImpl
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.FinancialPeriodService
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.ResourceAsString
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.util.LinkedMultiValueMap
import java.math.BigDecimal
import java.util.UUID

@WebMvcTest(FinancialPeriodController::class)
@Import(value = [FinancialPeriodMapperImpl::class])
class FinancialPeriodControllerTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var financialPeriodService: FinancialPeriodService

    @MockkBean
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should call create and return created`(@ResourceAsString("financial-period/create.json") payload: String) {

        val externalId = UUID.randomUUID()

        every { financialPeriodService.create(any()) } returns externalId

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost$ENDPOINT_URL/$externalId")
            }
        }

        verify(exactly = 1) { financialPeriodService.create(any()) }

        confirmVerified(financialPeriodService)
    }

    @Test
    fun `should call update and return ok`(@ResourceAsString("financial-period/update.json") payload: String) {

        val externalId = UUID.randomUUID()
        val expectedFinancialPeriod = createFinancialPeriod(externalId = externalId)

        every { financialPeriodRepository.findByExternalId(eq(externalId)) } returns expectedFinancialPeriod
        every { financialPeriodService.update(any<FinancialPeriod>()) } returns expectedFinancialPeriod

        val jsonResponse = mockMvc.put("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", externalId.toString())
            .containsEntry("name", "09/2024")
            .containsEntry("startingAt", "2024-09-01")
            .containsEntry("endingAt", "2024-09-30")
            .containsEntry("revenuesGoal", BigDecimal.ONE)
            .containsEntry("expensesGoal", BigDecimal.ONE)
            .containsEntry("status", "ACTIVE")

        verify(exactly = 1) { financialPeriodRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 1) { financialPeriodService.update(ofType<FinancialPeriod>()) }

        confirmVerified(financialPeriodService, financialPeriodRepository)
    }

    @Test
    fun `should call delete and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedFinancialPeriod = createFinancialPeriod(externalId = externalId)

        every { financialPeriodRepository.findByExternalId(externalId) } returns expectedFinancialPeriod
        every { financialPeriodService.delete(expectedFinancialPeriod) } just Runs

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { financialPeriodRepository.findByExternalId(externalId) }
        verify(exactly = 1) { financialPeriodService.delete(expectedFinancialPeriod) }

        confirmVerified(financialPeriodService, financialPeriodRepository)
    }

    @Test
    fun `should return not found if try to delete unknown cost center`() {

        val externalId = UUID.randomUUID()

        every { financialPeriodRepository.findByExternalId(externalId) } returns null

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { financialPeriodRepository.findByExternalId(externalId) }

        confirmVerified(financialPeriodService)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present`(
        @ResourceAsString("financial-period/invalid.json") payload: String
    ) {
        val requiredEntries = mapOf(
            "name" to "financial-period.name.is-null-or-blank",
            "startingAt" to "financial-period.starting-at.is-null",
            "endingAt" to "financial-period.ending-at.is-null"
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
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

        verify { financialPeriodService.create(any()) wasNot called }

        confirmVerified(financialPeriodService)
    }

    @Test
    fun `should return conflict if name is duplicated`(@ResourceAsString("financial-period/create.json") payload: String) {

        every { financialPeriodService.create(any()) } throws
                DuplicatedPropertyException("financial-period.errors.duplicated-name", "financial-period.name")

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.property", equalTo("financial-period.name"))
            jsonPath("\$.error", equalTo("financial-period.errors.duplicated-name"))
        }

        verify(exactly = 1) { financialPeriodService.create(any()) }

        confirmVerified(financialPeriodService)
    }

    @Test
    fun `should return bad request if dates are invalid`(
        @ResourceAsString("financial-period/create.json") payload: String
    ) {
        // TODO implement
    }

    @Test
    fun `should return bad request if dates overlap`(
        @ResourceAsString("financial-period/create.json") payload: String
    ) {
        // TODO implement
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedFinancialPeriod = createFinancialPeriod(externalId = externalId)

        every { financialPeriodRepository.findByExternalId(externalId) } returns expectedFinancialPeriod

        val jsonResponse = mockMvc.get("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", expectedFinancialPeriod.externalId!!.toString())
            .containsEntry("name", expectedFinancialPeriod.name)
            .containsEntry("startingAt", expectedFinancialPeriod.startingAt.toString())
            .containsEntry("endingAt", expectedFinancialPeriod.endingAt.toString())
            .containsEntry("revenuesGoal", expectedFinancialPeriod.revenuesGoal)
            .containsEntry("expensesGoal", expectedFinancialPeriod.expensesGoal)
            .containsEntry("status", expectedFinancialPeriod.status.name)

        verify(exactly = 1) { financialPeriodRepository.findByExternalId(externalId) }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { financialPeriodRepository.findByExternalId(externalId) } returns null

        mockMvc.get("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { financialPeriodRepository.findByExternalId(externalId) }

        confirmVerified(financialPeriodRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val pageRequest = PageRequest.of(0, 1)
        val financialPeriods = listOf(createFinancialPeriod())

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", pageRequest.pageNumber.toString())
        parameters.add("size", pageRequest.pageSize.toString())

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Some filter")

        val pageableSlot = slot<Pageable>()
        val specificationSlot = slot<Specification<FinancialPeriod>>()

        val thePage = PageImpl(financialPeriods, pageRequest, financialPeriods.size.toLong())

        every { financialPeriodRepository.findAll(capture(specificationSlot), capture(pageableSlot)) } returns thePage

        val jsonResponse = mockMvc.get(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
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

        verify(exactly = 1) {
            financialPeriodRepository.findAll(
                ofType<Specification<FinancialPeriod>>(),
                ofType<Pageable>()
            )
        }

        confirmVerified(financialPeriodRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/financial-periods"
    }
}