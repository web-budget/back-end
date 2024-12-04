package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.FinancialPeriodController
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapperImpl
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.services.registration.FinancialPeriodService
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.JsonPayload
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
import java.util.UUID

@WebMvcTest(FinancialPeriodController::class)
@Import(value = [FinancialPeriodMapperImpl::class])
class FinancialPeriodControllerUTest : BaseControllerIntegrationTest() {

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
    fun `should call create and return created`() {

        val externalId = UUID.randomUUID()

        every { financialPeriodService.create(any<FinancialPeriod>()) } returns externalId

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("financial-period/create")
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost$ENDPOINT_URL/$externalId")
            }
        }

        verify(exactly = 1) { financialPeriodService.create(ofType<FinancialPeriod>()) }

        confirmVerified(financialPeriodService)
    }

    @Test
    fun `should call update and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedFinancialPeriod = createFinancialPeriod(externalId = externalId)

        every { financialPeriodRepository.findByExternalId(eq(externalId)) } returns expectedFinancialPeriod
        every { financialPeriodService.update(any<FinancialPeriod>()) } returns expectedFinancialPeriod

        val jsonResponse = mockMvc.put("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("financial-period/update")
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", externalId.toString())
            .containsEntry("name", expectedFinancialPeriod.name)
            .containsEntry("startingAt", expectedFinancialPeriod.startingAt.toString())
            .containsEntry("endingAt", expectedFinancialPeriod.endingAt.toString())
            .containsEntry("revenuesGoal", expectedFinancialPeriod.revenuesGoal)
            .containsEntry("expensesGoal", expectedFinancialPeriod.expensesGoal)
            .containsEntry("status", expectedFinancialPeriod.status.name)

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
    fun `should return not found if try to delete unknown financial period`() {

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
    fun `should expect unprocessable entity if required fields are not present`() {
        val requiredEntries = mapOf(
            "name" to "is-null-or-blank",
            "startingAt" to "is-null",
            "endingAt" to "is-null"
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("financial-period/invalid")
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

        verify { financialPeriodService.create(any()) wasNot called }

        confirmVerified(financialPeriodService)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedFinancialPeriod = createFinancialPeriod(externalId = externalId)

        every { financialPeriodRepository.findByExternalId(eq(externalId)) } returns expectedFinancialPeriod

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
            .containsEntry("id", externalId.toString())
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
    fun `should return all active financial periods`() {

        val pageRequest = PageRequest.of(0, 1)
        val financialPeriods = listOf(createFinancialPeriod())

        val thePage = PageImpl(financialPeriods, pageRequest, financialPeriods.size.toLong())

        every { financialPeriodRepository.findByStatus(FinancialPeriod.Status.ACTIVE, any<Pageable>()) } returns thePage

        val jsonResponse = mockMvc.get("$ENDPOINT_URL/active") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
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
            .hasSize(1)

        verify(exactly = 1) {
            financialPeriodRepository.findByStatus(ofType<FinancialPeriod.Status>(), ofType<Pageable>())
        }

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
            financialPeriodRepository.findAll(ofType<Specification<FinancialPeriod>>(), ofType<Pageable>())
        }

        confirmVerified(financialPeriodRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/financial-periods"
    }
}
