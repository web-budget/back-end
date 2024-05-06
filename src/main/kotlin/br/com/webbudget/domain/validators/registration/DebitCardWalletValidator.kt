package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import org.springframework.stereotype.Component

@Component
@OnCreateValidation
@OnUpdateValidation
class DebitCardWalletValidator : CardValidator {

    override fun validate(value: Card) {
        TODO()
    }
}