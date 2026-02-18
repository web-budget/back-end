package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.infrastructure.config.DatabaseSchemas.FINANCIAL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "wallet_balances", schema = FINANCIAL)
class WalletBalance(
    @field:Column(name = "value", nullable = false)
    val value: BigDecimal,
    @field:Column(name = "old_balance", nullable = false)
    val oldBalance: BigDecimal,
    @field:Column(name = "new_balance", nullable = false)
    val newBalance: BigDecimal,
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "type", nullable = false, length = 6)
    val type: Type,
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "reason", nullable = false, length = 12)
    val reason: Reason,

    @field:ManyToOne
    @field:JoinColumn(name = "id_wallet", nullable = false)
    val wallet: Wallet,

    @field:ManyToOne
    @field:JoinColumn(name = "id_period_movement")
    val periodMovement: PeriodMovement? = null,

    @field:Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null
) : PersistentEntity<Long>() {

    enum class Type {
        CREDIT, DEBIT
    }

    enum class Reason {
        TRANSFERENCE, PAYMENT, REFUND, ADJUSTMENT
    }
}