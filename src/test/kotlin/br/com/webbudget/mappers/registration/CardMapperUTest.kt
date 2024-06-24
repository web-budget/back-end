package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.CardMapperImpl
import br.com.webbudget.application.mappers.registration.WalletMapperImpl
import br.com.webbudget.application.payloads.registration.CardCreateForm
import br.com.webbudget.application.payloads.registration.CardUpdateForm
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Card.Type.CREDIT
import br.com.webbudget.domain.entities.registration.Card.Type.DEBIT
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import br.com.webbudget.utilities.fixture.createCard
import br.com.webbudget.utilities.fixture.createWallet
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.util.ReflectionTestUtils
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class CardMapperUTest {

    @MockK
    private lateinit var walletRepository: WalletRepository

    private val cardMapper = CardMapperImpl()

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(cardMapper, "walletMapper", WalletMapperImpl())
        ReflectionTestUtils.setField(cardMapper, "walletRepository", walletRepository)
    }

    @ParameterizedTest
    @MethodSource("createFormObjects")
    fun `should map create form to domain object`(form: CardCreateForm, walletMockCalls: Int) {

        every { walletRepository.findByExternalId(any<UUID>()) } returns createWallet(externalId = form.wallet)

        val domainObject = cardMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.type).isEqualTo(form.type)
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.lastFourDigits).isEqualTo(form.lastFourDigits)
                assertThat(it.flag).isEqualTo(form.flag)
                assertThat(it.invoicePaymentDay).isEqualTo(form.invoicePaymentDay)
            })

        verify(exactly = walletMockCalls) { walletRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(walletRepository)
    }

    @ParameterizedTest
    @MethodSource("updateFormObjects")
    fun `should map update form to domain object`(form: CardUpdateForm, domainObject: Card, walletMockCalls: Int) {

        every { walletRepository.findByExternalId(any<UUID>()) } returns createWallet(externalId = form.wallet)

        cardMapper.map(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.lastFourDigits).isEqualTo(form.lastFourDigits)
                assertThat(it.flag).isEqualTo(form.flag)
                assertThat(it.invoicePaymentDay).isEqualTo(form.invoicePaymentDay)
            })

        verify(exactly = walletMockCalls) { walletRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(walletRepository)
    }

    @ParameterizedTest
    @MethodSource("viewObjects")
    fun `should map domain object to view`(domainObject: Card) {

        val externalId = UUID.randomUUID()

        domainObject.apply {
            this.id = 1L
            this.externalId = externalId
        }

        val view = cardMapper.map(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.id).isEqualTo(domainObject.externalId)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.lastFourDigits).isEqualTo(domainObject.lastFourDigits)
                assertThat(it.flag).isEqualTo(domainObject.flag)
                assertThat(it.invoicePaymentDay).isEqualTo(domainObject.invoicePaymentDay)
            })
    }

    companion object {

        @JvmStatic
        fun createFormObjects(): Stream<Arguments> {

            val creditCardForm = CardCreateForm("Credit", "4321", 1, CREDIT, null, "Visa")
            val debitCardForm = CardCreateForm("Debit", "1234", 1, DEBIT, UUID.randomUUID(), "Master")

            return Stream.of(
                Arguments.of(creditCardForm, 0),
                Arguments.of(debitCardForm, 1)
            )
        }

        @JvmStatic
        fun updateFormObjects(): Stream<Arguments> {

            val creditCardForm = CardUpdateForm("The Credit", "2222", 3, null, "M", true)
            val debitCardForm = CardUpdateForm("The Debit", "1111", 2, UUID.randomUUID(), "V", true)

            val debitCard = createCard(type = DEBIT, wallet = createWallet(id = 1L, externalId = UUID.randomUUID()))

            return Stream.of(
                Arguments.of(creditCardForm, createCard(), 0),
                Arguments.of(debitCardForm, debitCard, 1)
            )
        }

        @JvmStatic
        fun viewObjects(): Stream<Arguments> {
            val wallet = createWallet(id = 1L, externalId = UUID.randomUUID())
            return Stream.of(Arguments.of(createCard()), Arguments.of(createCard(type = DEBIT, wallet = wallet)))
        }
    }
}