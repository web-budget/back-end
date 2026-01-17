package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.CardCreateForm
import br.com.webbudget.application.payloads.registration.CardListView
import br.com.webbudget.application.payloads.registration.CardUpdateForm
import br.com.webbudget.application.payloads.registration.CardView
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CardMapper(
    private val walletMapper: WalletMapper,
    private val walletRepository: WalletRepository
) {

    fun mapToView(card: Card): CardView = CardView(
        id = card.externalId!!,
        name = card.name,
        lastFourDigits = card.lastFourDigits,
        type = card.type,
        active = card.active,
        flag = card.flag,
        invoicePaymentDay = card.invoicePaymentDay,
        wallet = card.wallet?.let { walletMapper.mapToListView(it) }
    )

    fun mapToListView(card: Card): CardListView = CardListView(
        id = card.externalId!!,
        name = card.name,
        type = card.type,
        active = card.active,
        flag = card.flag
    )

    fun mapToDomain(form: CardCreateForm): Card = Card(
        name = form.name!!,
        lastFourDigits = form.lastFourDigits!!,
        type = form.type!!,
        invoicePaymentDay = form.invoicePaymentDay,
        flag = form.flag,
        wallet = mapWallet(form.wallet)
    )

    fun mapToDomain(form: CardUpdateForm, card: Card) = card.apply {
        this.name = form.name!!
        this.lastFourDigits = form.lastFourDigits!!
        this.invoicePaymentDay = form.invoicePaymentDay
        this.active = form.active!!
        this.flag = form.flag
        this.wallet = mapWallet(form.wallet)
    }

    private fun mapWallet(id: UUID?): Wallet? = id?.let {
        walletRepository.findByExternalId(id) ?: throw ResourceNotFoundException()
    }
}
