package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.registration.CreditCardInvoicePaymentDayValidator
import br.com.webbudget.utilities.fixture.createCard
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class CreditCardInvoicePaymentDayValidatorUTest {

    @InjectMockKs
    private lateinit var creditCardInvoicePaymentDayValidator: CreditCardInvoicePaymentDayValidator

    @Test
    fun `should pass when credit card has valid invoice payment day`() {

        val card = createCard(type = Card.Type.CREDIT, invoicePaymentDay = 1)

        assertThatNoException()
            .isThrownBy { creditCardInvoicePaymentDayValidator.validate(card) }
    }

    @ParameterizedTest
    @ValueSource(ints = [Int.MIN_VALUE, -1, 0, 32, Int.MAX_VALUE])
    fun `should pass when credit card has valid invoice payment day`(invoicePaymentDay: Int) {

        val card = createCard(type = Card.Type.CREDIT, invoicePaymentDay = invoicePaymentDay)

        assertThatThrownBy { creditCardInvoicePaymentDayValidator.validate(card) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    fun `should thrown exception when credit card has no invoice payment day`() {

        val card = createCard(type = Card.Type.CREDIT, invoicePaymentDay = null)

        assertThatThrownBy { creditCardInvoicePaymentDayValidator.validate(card) }
            .isInstanceOf(BusinessException::class.java)
    }
}