package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.entities.registration.Wallet.Type.PERSONAL
import java.math.BigDecimal
import java.util.UUID

object WalletFixture {

    fun create() = create("Wallet", PERSONAL)

    fun create(
        id: Long,
        externalId: UUID,
        name: String,
        type: Wallet.Type,
        bankName: String? = null,
        agency: String? = null,
        number: String? = null
    ) = create(name, type, bankName, agency, number).apply {
        this.id = id
        this.externalId = externalId
    }

    fun create(
        name: String,
        type: Wallet.Type,
        bankName: String? = null,
        agency: String? = null,
        number: String? = null
    ) = Wallet(name, type, BigDecimal.TEN, true, "Some wallet", bankName, agency, number)
}
