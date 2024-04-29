package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.infrastructure.repository.registration.CardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CardService(
    private val cardRepository: CardRepository,
    private val cardValidationService: CardValidationService
) {

    @Transactional
    fun create(card: Card): UUID {
        cardValidationService.validateOnCreate(card)
        val created = cardRepository.persist(card)
        return created.externalId!!
    }

    @Transactional
    fun update(card: Card): Card {
        cardValidationService.validateOnUpdate(card)
        return cardRepository.merge(card)
    }

    @Transactional
    fun delete(card: Card) {
        cardRepository.delete(card)
    }
}
