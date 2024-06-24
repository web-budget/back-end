package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.CardController
import br.com.webbudget.application.mappers.registration.CardMapperImpl
import br.com.webbudget.application.mappers.registration.WalletMapperImpl
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CardService
import br.com.webbudget.infrastructure.repository.registration.CardRepository
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.Authorities
import br.com.webbudget.utilities.ResourceAsString
import br.com.webbudget.utilities.fixture.createCard
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

@WebMvcTest(CardController::class)
@Import(value = [CardMapperImpl::class, WalletMapperImpl::class])
class CardControllerUTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var cardService: CardService

    @MockkBean
    private lateinit var walletRepository: WalletRepository

    @MockkBean
    private lateinit var cardRepository: CardRepository

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should create credit card and return created`(@ResourceAsString("card/create-credit.json") payload: String) {

        val externalId = UUID.randomUUID()

        every { cardService.create(any<Card>()) } returns externalId

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

        verify(exactly = 1) { cardService.create(ofType<Card>()) }

        confirmVerified(cardService)
    }

    @Test
    fun `should create debit card and return created`(@ResourceAsString("card/create-debit.json") payload: String) {

        val externalId = UUID.randomUUID()

        every { cardService.create(any<Card>()) } returns externalId
        every { walletRepository.findByExternalId(any<UUID>()) } returns createWallet()

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

        verify(exactly = 1) { cardService.create(ofType<Card>()) }
        verify(exactly = 1) { walletRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(cardService, walletRepository)
    }

    @Test
    fun `should search wallet before create debit card and return created`(
        @ResourceAsString("card/create-credit.json") payload: String
    ) {

        val externalId = UUID.randomUUID()

        every { cardService.create(any<Card>()) } returns externalId

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

        verify(exactly = 1) { cardService.create(ofType<Card>()) }

        confirmVerified(cardService)
    }

    @Test
    fun `should call update and return ok`(@ResourceAsString("card/update.json") payload: String) {

        val externalId = UUID.randomUUID()
        val expectedCard = createCard(externalId = externalId)

        every { cardRepository.findByExternalId(eq(externalId)) } returns expectedCard
        every { cardService.update(any<Card>()) } returns expectedCard

        val jsonResponse = mockMvc.put("${ENDPOINT_URL}/$externalId") {
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
            .containsEntry("active", false)
            .containsEntry("name", "The Card")
            .containsEntry("lastFourDigits", "1234")
            .containsEntry("flag", "Master")
            .containsEntry("invoicePaymentDay", 1)

        verify(exactly = 1) { cardRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 1) { cardService.update(ofType<Card>()) }

        confirmVerified(cardService, cardRepository)
    }

    @Test
    fun `should call delete and return ok`() {

        val externalId = UUID.randomUUID()
        val expectedCard = createCard(externalId = externalId)

        every { cardRepository.findByExternalId(eq(externalId)) } returns expectedCard
        every { cardService.delete(eq(expectedCard)) } just Runs

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { cardRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 1) { cardService.delete(eq(expectedCard)) }

        confirmVerified(cardService, cardRepository)
    }

    @Test
    fun `should expect not found if try to delete something that does not exist`() {

        val externalId = UUID.randomUUID()

        every { cardRepository.findByExternalId(eq(externalId)) } returns null

        mockMvc.delete("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { cardRepository.findByExternalId(eq(externalId)) }
        verify(exactly = 0) { cardService.delete(ofType<Card>()) }

        confirmVerified(cardService, cardRepository)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present for credit card`(
        @ResourceAsString("card/invalid-credit.json") payload: String
    ) {

        val requiredEntries = mapOf(
            "type" to "card.errors.type-is-null",
            "name" to "card.errors.name-is-blank",
            "lastFourDigits" to "card.errors.last-four-digits-is-blank"
        )

        val response = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .node("errors")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)

        verify { cardService.create(ofType<Card>()) wasNot called }

        confirmVerified(cardService)
    }

    @Test
    fun `should expect unprocessable entity if required fields are not present for debit card`(
        @ResourceAsString("card/invalid-debit.json") payload: String
    ) {

        val requiredEntries = mapOf(
            "type" to "card.errors.type-is-null",
            "name" to "card.errors.name-is-blank",
            "lastFourDigits" to "card.errors.last-four-digits-is-blank"
        )

        val response = mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andReturn()
            .response
            .contentAsString

        assertThatJson(response)
            .node("errors")
            .isObject
            .hasSize(requiredEntries.size)
            .containsExactlyInAnyOrderEntriesOf(requiredEntries)

        verify { cardService.create(ofType<Card>()) wasNot called }

        confirmVerified(cardService)
    }

    @Test
    fun `should return conflict if number and type are duplicated`(
        @ResourceAsString("card/create-credit.json") payload: String
    ) {

        every { cardService.create(any<Card>()) } throws
                DuplicatedPropertyException("card.errors.duplicated-card", "card.type-and-last-four-digits")

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }.andExpect {
            jsonPath("\$.property", equalTo("card.type-and-last-four-digits"))
            jsonPath("\$.error", equalTo("card.errors.duplicated-card"))
        }

        verify(exactly = 1) { cardService.create(ofType<Card>()) }

        confirmVerified(cardService)
    }

    @Test
    fun `should return bad request if debit card has no wallet`(
        @ResourceAsString("card/create-debit-invalid.json") payload: String
    ) {

        every { cardService.create(any<Card>()) } throws BusinessException(
            "Debit card has no wallet",
            "card.errors.debit-without-wallet"
        )

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isBadRequest() }
        }.andExpect {
            jsonPath("\$.detail", equalTo("card.errors.debit-without-wallet"))
            jsonPath("\$.error", equalTo("Debit card has no wallet"))
        }

        verify(exactly = 1) { cardService.create(ofType<Card>()) }

        confirmVerified(cardService)
    }

    @Test
    fun `should return bad request if credit card has invalid invoice payment day`(
        @ResourceAsString("card/create-credit-invalid.json") payload: String
    ) {
        every { cardService.create(any<Card>()) } throws BusinessException(
            "Credit card has has invalid invoice payment day",
            "card.errors.credit-invalid-payment-day"
        )

        mockMvc.post(ENDPOINT_URL) {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isBadRequest() }
        }.andExpect {
            jsonPath("\$.detail", equalTo("card.errors.credit-invalid-payment-day"))
            jsonPath("\$.error", equalTo("Credit card has has invalid invoice payment day"))
        }

        verify(exactly = 1) { cardService.create(ofType<Card>()) }

        confirmVerified(cardService)
    }

    @Test
    fun `should call find by id and expect ok`() {

        val externalId = UUID.randomUUID()
        val expectedCard = createCard(externalId = externalId)

        every { cardRepository.findByExternalId(externalId) } returns expectedCard

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
            .containsEntry("active", true)
            .containsEntry("name", "Card")
            .containsEntry("lastFourDigits", "1234")
            .containsEntry("flag", "Flag")
            .containsEntry("type", "CREDIT")
            .containsEntry("invoicePaymentDay", 1)

        verify(exactly = 1) { cardRepository.findByExternalId(eq(externalId)) }

        confirmVerified(cardRepository)
    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

        val externalId = UUID.randomUUID()

        every { cardRepository.findByExternalId(externalId) } returns null

        mockMvc.get("${ENDPOINT_URL}/$externalId") {
            with(jwt().authorities(Authorities.REGISTRATION))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify(exactly = 1) { cardRepository.findByExternalId(eq(externalId)) }

        confirmVerified(cardRepository)
    }

    @Test
    fun `should call get and expect paged result`() {

        val pageRequest = PageRequest.of(0, 1)
        val cards = listOf(createCard())

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", pageRequest.pageNumber.toString())
        parameters.add("size", pageRequest.pageSize.toString())

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Some filter")

        val pageableSlot = slot<Pageable>()
        val specificationSlot = slot<Specification<Card>>()

        val thePage = PageImpl(cards, pageRequest, cards.size.toLong())

        every { cardRepository.findAll(capture(specificationSlot), capture(pageableSlot)) } returns thePage

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

        verify(exactly = 1) { cardRepository.findAll(ofType<Specification<Card>>(), ofType<Pageable>()) }

        confirmVerified(cardRepository)
    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/cards"
    }
}