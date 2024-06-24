package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.CostCenterController
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.ResourceAsString
import br.com.webbudget.utilities.fixture.createCostCenter
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
import java.util.UUID

@WebMvcTest(CostCenterController::class)
@Import(value = [CostCenterMapperImpl::class])
class CostCenterControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var costCenterService: CostCenterService

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
    fun `should call create and return created`(@ResourceAsString("cost-center/create.json") payload: String) {

        val externalId = UUID.randomUUID()

        every { costCenterService.create(any()) } returns externalId

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

        verify(exactly = 1) { costCenterService.create(any()) }

        confirmVerified(costCenterService)
    }

    @Test
    fun `should call update and return ok`(@ResourceAsString("cost-center/update.json") payload: String) {

        val externalId = UUID.randomUUID()
        val expectedCostCenter = createCostCenter(externalId = externalId)

        every { costCenterRepository.findByExternalId(eq(externalId)) } returns expectedCostCenter
        every { costCenterService.update(any<CostCenter>()) } returns expectedCostCenter

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
            .containsEntry("name", "Car")
            .containsEntry("active", false)
            .containsEntry("description", "Updated description")

        verify(exactly = 1) { costCenterRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 1) { costCenterService.update(ofType<CostCenter>()) }

        confirmVerified(costCenterService, costCenterRepository)
    }

    @Test
    fun `should call delete and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedCostCenter = createCostCenter(externalId = externalId)

        every { costCenterRepository.findByExternalId(externalId) } returns expectedCostCenter
        every { costCenterService.delete(expectedCostCenter) } just Runs

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { costCenterRepository.findByExternalId(externalId) }
        verify(exactly = 1) { costCenterService.delete(expectedCostCenter) }

        confirmVerified(costCenterService, costCenterRepository)
    }

    @Test
    fun `should return not found if try to delete unknown cost center`() {

        val externalId = UUID.randomUUID()

        every { costCenterRepository.findByExternalId(externalId) } returns null

        mockMvc.delete("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { costCenterRepository.findByExternalId(externalId) }

        confirmVerified(costCenterService)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present`(
        @ResourceAsString("cost-center/invalid.json") payload: String
    ) {
        val requiredEntries = mapOf("name" to "cost-center.errors.name-is-blank")

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

        verify { costCenterService.create(any()) wasNot called }

        confirmVerified(costCenterService)
    }

    @Test
    fun `should return conflict if name is duplicated`(@ResourceAsString("cost-center/create.json") payload: String) {

        every { costCenterService.create(any()) } throws
                DuplicatedPropertyException("cost-center.errors.duplicated-name", "cost-center.name")

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.property", equalTo("cost-center.name"))
            jsonPath("\$.error", equalTo("cost-center.errors.duplicated-name"))
        }

        verify(exactly = 1) { costCenterService.create(any()) }

        confirmVerified(costCenterService)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedCostCenter = createCostCenter(externalId = externalId)

        every { costCenterRepository.findByExternalId(externalId) } returns expectedCostCenter

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
            .containsEntry("id", expectedCostCenter.externalId!!.toString())
            .containsEntry("name", "Cost Center")
            .containsEntry("description", "Some description")
            .containsEntry("active", true)

        verify(exactly = 1) { costCenterRepository.findByExternalId(externalId) }

        confirmVerified(costCenterRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { costCenterRepository.findByExternalId(externalId) } returns null

        mockMvc.get("$ENDPOINT_URL/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { costCenterRepository.findByExternalId(externalId) }

        confirmVerified(costCenterRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val pageRequest = PageRequest.of(0, 1)
        val costCenters = listOf(createCostCenter())

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", pageRequest.pageNumber.toString())
        parameters.add("size", pageRequest.pageSize.toString())

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Some filter")

        val pageableSlot = slot<Pageable>()
        val specificationSlot = slot<Specification<CostCenter>>()

        val thePage = PageImpl(costCenters, pageRequest, costCenters.size.toLong())

        every { costCenterRepository.findAll(capture(specificationSlot), capture(pageableSlot)) } returns thePage

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

        verify(exactly = 1) { costCenterRepository.findAll(ofType<Specification<CostCenter>>(), ofType<Pageable>()) }

        confirmVerified(costCenterRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/cost-centers"
    }
}
