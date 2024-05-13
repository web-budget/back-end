package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.CardRepository
import org.springframework.stereotype.Component

@Component
@OnCreateValidation
@OnUpdateValidation
class CardTypeAndNumberValidator(
    private val cardRepository: CardRepository
) : CardValidator {

    override fun validate(value: Card) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: Card) {
        cardRepository.findByTypeAndLastFourDigitsAndExternalIdNot(value.type, value.lastFourDigits, value.externalId!!)
            ?.let { throw DuplicatedPropertyException("card.errors.duplicated-card", "card.type-and-last-four-digits") }
    }

    private fun validateNotSaved(value: Card) {
        cardRepository.findByTypeAndLastFourDigits(value.type, value.lastFourDigits)
            ?.let { throw DuplicatedPropertyException("card.errors.duplicated-card", "card.type-and-last-four-digits") }
    }
}