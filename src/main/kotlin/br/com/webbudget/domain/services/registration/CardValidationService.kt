package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.CardValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CardValidationService(
    @OnCreateValidation
    private val creationValidators: List<CardValidator>,
    @OnUpdateValidation
    private val updateValidators: List<CardValidator>
) : ValidationService<Card> {

    override fun validateOnCreate(value: Card) {
        creationValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: Card) {
        updateValidators.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: Card) {
        TODO("Not yet implemented")
    }
}
