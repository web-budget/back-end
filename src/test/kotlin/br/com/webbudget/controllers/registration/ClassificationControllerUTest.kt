package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.ClassificationController
import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.services.registration.ClassificationService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.utilities.JsonPayload
import br.com.webbudget.utilities.Roles
import br.com.webbudget.utilities.fixtures.createCostCenter
import br.com.webbudget.utilities.fixtures.createClassification
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.util.LinkedMultiValueMap
import java.util.UUID

@WithMockUser(roles = [Roles.REGISTRATION])
@WebMvcTest(ClassificationController::class)
@Import(value = [ClassificationMapper::class, CostCenterMapper::class])
class ClassificationControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var classificationService: ClassificationService

    @MockkBean
    private lateinit var classificationRepository: ClassificationRepository

    @MockkBean
    private lateinit var costCenterRepository: CostCenterRepository

    @Test
    @WithMockUser(roles = [])
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    fun `should call create and expect created`() {

        val externalId = UUID.randomUUID()

        every { classificationService.create(any<Classification>()) } returns externalId
        every { costCenterRepository.findByExternalId(any<UUID>()) } returns createCostCenter()

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("movement-class/create")
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost${ENDPOINT_URL}/$externalId")
            }
        }

        verify(exactly = 1) { classificationService.create(ofType<Classification>()) }
        verify(exactly = 1) { costCenterRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationService, costCenterRepository)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present`() {
        val requiredEntries = mapOf(
            "name" to "is-null-or-blank",
            "type" to "is-null",
            "costCenter" to "is-null"
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("movement-class/invalid")
        }.andExpect {
            status { isUnprocessableContent() }
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

        verify(exactly = 0) { classificationService.create(ofType<Classification>()) }
        verify(exactly = 0) { costCenterRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationService, costCenterRepository)
    }

    @Test
    fun `should call update and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedMovementClass = createClassification(externalId = externalId)
        val expectedCostCenter = createCostCenter(externalId = externalId)

        every { classificationService.update(any<Classification>()) } returns expectedMovementClass
        every { classificationRepository.findByExternalId(any<UUID>()) } returns expectedMovementClass
        every { costCenterRepository.findByExternalId(any<UUID>()) } returns expectedCostCenter

        val jsonResponse = mockMvc.put("$ENDPOINT_URL/$externalId") {
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("movement-class/update")
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", externalId.toString())
            .containsEntry("name", "Manutenção")
            .containsEntry("type", "INCOME")
            .containsEntry("budget", 2000)
            .containsEntry("description", "Manutenção do carro")
            .containsEntry("active", true)

        assertThatJson(jsonResponse)
            .node("costCenter")
            .isObject
            .containsEntry("id", externalId.toString())

        verify(exactly = 1) { classificationService.update(ofType<Classification>()) }
        verify(exactly = 1) { costCenterRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(classificationService, costCenterRepository, classificationRepository)
    }

    @Test
    fun `should call delete and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedMovementClass = createClassification(externalId = externalId)

        every { classificationRepository.findByExternalId(eq(externalId)) } returns expectedMovementClass
        every { classificationService.delete(eq(expectedMovementClass)) } just Runs

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { classificationRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 1) { classificationService.delete(eq(expectedMovementClass)) }

        confirmVerified(classificationService, classificationRepository)
    }

    @Test
    fun `should expect not found if try to delete something that does not exist`() {

        val externalId = UUID.randomUUID()

        every { classificationRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { classificationRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 0) { classificationService.delete(ofType<Classification>()) }

        confirmVerified(classificationService, classificationRepository)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedMovementClass = createClassification(externalId = externalId)

        every { classificationRepository.findByExternalId(externalId) } returns expectedMovementClass

        val jsonResponse = mockMvc.get("${ENDPOINT_URL}/$externalId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", externalId.toString())
            .containsEntry("name", "Classification")
            .containsEntry("type", "INCOME")
            .containsEntry("description", "Some description")
            .containsEntry("active", true)

        assertThatJson(jsonResponse)
            .node("costCenter")
            .isObject
            .isNotNull

        verify(exactly = 1) { classificationRepository.findByExternalId(eq(externalId)) }

        confirmVerified(classificationRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { classificationRepository.findByExternalId(externalId) } returns null

        mockMvc.get("${ENDPOINT_URL}/$externalId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { classificationRepository.findByExternalId(eq(externalId)) }

        confirmVerified(classificationRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val pageRequest = PageRequest.of(0, 1)

        val movementClasses = listOf(createClassification())

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", pageRequest.pageNumber.toString())
        parameters.add("size", pageRequest.pageSize.toString())

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Some filter")

        val pageableSlot = slot<Pageable>()
        val specificationSlot = slot<Specification<Classification>>()

        val thePage = PageImpl(movementClasses, pageRequest, movementClasses.size.toLong())

        every {
            classificationRepository.findAll(capture(specificationSlot), capture(pageableSlot))
        } returns thePage

        val jsonResponse = mockMvc.get(ENDPOINT_URL) {
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
            classificationRepository.findAll(
                ofType<Specification<Classification>>(),
                ofType<Pageable>()
            )
        }

        confirmVerified(classificationRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/classifications"
    }
}