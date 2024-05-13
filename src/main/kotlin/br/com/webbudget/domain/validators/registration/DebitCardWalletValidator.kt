package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Card.Type
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import org.springframework.stereotype.Component

@Component
@OnCreateValidation
@OnUpdateValidation
class DebitCardWalletValidator : CardValidator {

    override fun validate(value: Card) {
        check(value.type == Type.DEBIT && value.wallet != null) {
            throw IllegalStateException("card.errors.debit-without-wallet")
        }
    }
}