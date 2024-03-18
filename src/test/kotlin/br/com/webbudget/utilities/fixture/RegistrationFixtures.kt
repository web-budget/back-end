package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.Wallet
import java.math.BigDecimal
import java.util.UUID

@Suppress("LongParameterList")
fun createWallet(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "Wallet",
    type: Wallet.Type = Wallet.Type.BANK_ACCOUNT,
    balance: BigDecimal = BigDecimal.ZERO,
    active: Boolean = true,
    description: String = "Some description",
    bank: String? = "Bank",
    agency: String? = "123",
    number: String? = "456789"
) = Wallet(name, type, balance, active, description, bank, agency, number)
    .apply {
        this.id = id
        this.externalId = externalId
    }

@Suppress("LongParameterList")
fun createCostCenter(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "Cost Center",
    active: Boolean = true,
    description: String = "Some description"
) = CostCenter(name, active, description)
    .apply {
        this.id = id
        this.externalId = externalId
    }
