package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.Card.Type
import br.com.webbudget.domain.entities.registration.Card.Type.CREDIT
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.CardTypeAndNumberValidator
import br.com.webbudget.infrastructure.repository.registration.CardRepository
import br.com.webbudget.utilities.fixture.createCard
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class CardTypeAndNumberValidatorUTest {

    @MockK
    private lateinit var cardRepository: CardRepository

    @InjectMockKs
    private lateinit var cardTypeAndNumberValidator: CardTypeAndNumberValidator

    @ParameterizedTest
    @MethodSource("testParams")
    fun `should fail for different entities and equal name`(type: Type, lastFourDigits: String) {

        every { cardRepository.findByTypeAndLastFourDigits(type, lastFourDigits) } returns createCard()

        val toValidate = createCard(id = null, externalId = null, type = type, lastFourDigits = lastFourDigits)

        assertThatThrownBy { cardTypeAndNumberValidator.validate(toValidate) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("card.errors.duplicated-card")

        verify(exactly = 1) { cardRepository.findByTypeAndLastFourDigits(eq(type), eq(lastFourDigits)) }

        confirmVerified(cardRepository)
    }

    @ParameterizedTest
    @MethodSource("testParams")
    fun `should not fail if entities are equal`(type: Type, lastFourDigits: String) {

        val externalId = UUID.randomUUID()

        every {
            cardRepository.findByTypeAndLastFourDigitsAndExternalIdNot(type, lastFourDigits, externalId)
        } returns null

        val toValidate = createCard(id = 1L, externalId = externalId, type = type, lastFourDigits = lastFourDigits)

        assertThatNoException()
            .isThrownBy { cardTypeAndNumberValidator.validate(toValidate) }

        verify(exactly = 1) {
            cardRepository.findByTypeAndLastFourDigitsAndExternalIdNot(eq(type), eq(lastFourDigits), eq(externalId))
        }

        confirmVerified(cardRepository)
    }

    companion object {

        @JvmStatic
        fun testParams(): Stream<Arguments> = Stream.of(Arguments.of(CREDIT, "1234"), Arguments.of(CREDIT, "4321"))
    }
}