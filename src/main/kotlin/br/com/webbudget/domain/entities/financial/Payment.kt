package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.Card
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
import java.time.LocalDate

@Entity
@Table(name = "payments", schema = FINANCIAL)
class Payment(
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "method", nullable = false, length = 11)
    val method: Method,
    @field:Column(name = "value_paid", nullable = false)
    val valuePaid: BigDecimal,

    @field:Column(name = "discount", nullable = false)
    val discount: BigDecimal = BigDecimal.ZERO,
    @field:Column(name = "paid_at", nullable = false)
    val paidAt: LocalDate? = LocalDate.now(),

    @field:ManyToOne
    @field:JoinColumn(name = "id_card")
    val card: Card? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "id_wallet")
    val wallet: Wallet? = null
) : PersistentEntity<Long>() {

    enum class Method {
        DEBIT_CARD, CREDIT_CARD, CASH
    }
}