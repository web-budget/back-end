package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.CardValidator
import br.com.webbudget.infrastructure.repository.registration.CardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CardService(
    private val cardRepository: CardRepository,
    @OnCreateValidation
    private val creationValidators: List<CardValidator>,
    @OnUpdateValidation
    private val updateValidators: List<CardValidator>
) {

    @Transactional
    fun create(card: Card): UUID {

        creationValidators.forEach { it.validate(card) }

        val created = cardRepository.persist(card)
        return created.externalId!!
    }

    @Transactional
    fun update(card: Card): Card {

        updateValidators.forEach { it.validate(card) }

        return cardRepository.merge(card)
    }

    @Transactional
    fun delete(card: Card) {
        cardRepository.delete(card)
    }
}
