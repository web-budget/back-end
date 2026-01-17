package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.payloads.registration.WalletCreateForm
import br.com.webbudget.application.payloads.registration.WalletListView
import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.application.payloads.registration.WalletView
import br.com.webbudget.domain.entities.registration.Wallet
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class WalletMapper {

    fun mapToView(wallet: Wallet): WalletView = WalletView(
        id = wallet.externalId!!,
        name = wallet.name,
        type = wallet.type,
        bank = wallet.bank,
        agency = wallet.agency,
        number = wallet.number,
        description = wallet.description,
        active = wallet.active,
        currentBalance = wallet.currentBalance ?: BigDecimal.ZERO
    )

    fun mapToListView(wallet: Wallet): WalletListView = WalletListView(
        id = wallet.externalId!!,
        name = wallet.name,
        type = wallet.type,
        active = wallet.active
    )

    fun mapToDomain(form: WalletCreateForm): Wallet = Wallet(
        name = form.name!!,
        type = form.type!!,
        currentBalance = BigDecimal.ZERO,
        description = form.description,
        bank = form.bank,
        agency = form.agency,
        number = form.number
    )

    fun mapToDomain(form: WalletUpdateForm, wallet: Wallet) = wallet.apply {
        this.name = form.name!!
        this.active = form.active!!
        this.description = form.description
        this.bank = form.bank
        this.agency = form.agency
        this.number = form.number
    }
}
