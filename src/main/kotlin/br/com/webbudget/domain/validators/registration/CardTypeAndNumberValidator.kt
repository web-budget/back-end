package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.exceptions.ConflictingPropertyException
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
            ?.let {
                throw ConflictingPropertyException(
                    mapOf(
                        "card.type" to value.type,
                        "card.last-four-digits" to value.lastFourDigits
                    )
                )
            }
    }

    private fun validateNotSaved(value: Card) {
        cardRepository.findByTypeAndLastFourDigits(value.type, value.lastFourDigits)
            ?.let {
                throw ConflictingPropertyException(
                    mapOf(
                        "card.type" to value.type,
                        "card.last-four-digits" to value.lastFourDigits
                    )
                )
            }
    }
}