package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.WalletController
import br.com.webbudget.application.mappers.registration.WalletMapperImpl
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.services.registration.WalletService
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.JsonPayload
import br.com.webbudget.utilities.fixture.createWallet
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

@WebMvcTest(WalletController::class)
@Import(value = [WalletMapperImpl::class])
class WalletControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var walletService: WalletService

    @MockkBean
    private lateinit var walletRepository: WalletRepository

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

        every { walletService.create(any()) } returns externalId

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("wallet/create")
        }.andExpect {
            status { isCreated() }
        }.andExpect {
            header {
                stringValues("Location", "http://localhost${ENDPOINT_URL}/$externalId")
            }
        }

        verify(exactly = 1) { walletService.create(any()) }

        confirmVerified(walletService)
    }

    @Test
    fun `should call update and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedWallet = createWallet()

        every { walletRepository.findByExternalId(externalId) } returns expectedWallet
        every { walletService.update(any()) } returns expectedWallet

        val jsonResponse = mockMvc.put("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("wallet/update")
        }.andExpect {
            status { isOk() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(jsonResponse)
            .isObject
            .containsEntry("id", expectedWallet.externalId!!.toString())
            .containsEntry("name", "Another Wallet")
            .containsEntry("active", false)
            .containsEntry("description", "Another some wallet")
            .containsEntry("type", "BANK_ACCOUNT")

        verify(exactly = 1) { walletRepository.findByExternalId(externalId) }
        verify(exactly = 1) { walletService.update(any()) }

        confirmVerified(walletService, walletRepository)
    }

    @Test
    fun `should call delete and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedWallet = createWallet()

        every { walletRepository.findByExternalId(externalId) } returns expectedWallet
        every { walletService.delete(expectedWallet) } just Runs

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { walletRepository.findByExternalId(externalId) }
        verify(exactly = 1) { walletService.delete(expectedWallet) }

        confirmVerified(walletService, walletRepository)
    }

    @Test
    fun `should return not found if try to delete unknown wallet`() {

        val externalId = UUID.randomUUID()

        every { walletRepository.findByExternalId(externalId) } returns null

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { walletRepository.findByExternalId(externalId) }

        confirmVerified(walletService)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present`() {

        val requiredEntries = mapOf(
            "name" to "is-null-or-blank",
            "type" to "is-null"
        )

        val jsonResponse = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = JsonPayload("wallet/invalid")
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

        verify { walletService.create(any()) wasNot called }

        confirmVerified(walletService)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedWallet = createWallet()

        every { walletRepository.findByExternalId(externalId) } returns expectedWallet

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
            .containsEntry("id", expectedWallet.externalId!!.toString())
            .containsEntry("name", "Wallet")
            .containsEntry("type", "BANK_ACCOUNT")
            .containsEntry("description", "Some description")
            .containsEntry("active", true)

        verify(exactly = 1) { walletRepository.findByExternalId(externalId) }

        confirmVerified(walletRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { walletRepository.findByExternalId(externalId) } returns null

        mockMvc.get("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { walletRepository.findByExternalId(externalId) }

        confirmVerified(walletRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val pageRequest = PageRequest.of(0, 1)
        val wallets = listOf(createWallet())

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", pageRequest.pageNumber.toString())
        parameters.add("size", pageRequest.pageSize.toString())

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Some filter")

        val pageableSlot = slot<Pageable>()
        val specificationSlot = slot<Specification<Wallet>>()

        val thePage = PageImpl(wallets, pageRequest, wallets.size.toLong())

        every { walletRepository.findAll(capture(specificationSlot), capture(pageableSlot)) } returns thePage

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

        verify(exactly = 1) { walletRepository.findAll(ofType<Specification<Wallet>>(), ofType<Pageable>()) }

        confirmVerified(walletRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/wallets"
    }
}
