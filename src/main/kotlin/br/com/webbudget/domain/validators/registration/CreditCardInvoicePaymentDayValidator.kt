package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Card.Type
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import org.springframework.stereotype.Component

@Component
@OnCreateValidation
@OnUpdateValidation
class CreditCardInvoicePaymentDayValidator : CardValidator {

    override fun validate(value: Card) {

        val validDateRange = 1..31
        val paymentDay = value.invoicePaymentDay

        if (value.type == Type.CREDIT && (paymentDay == null || !validDateRange.contains(paymentDay))) {
            throw BusinessException(
                "Credit card has has invalid invoice payment day",
                "card.errors.credit-invalid-payment-day"
            )
        }
    }
}