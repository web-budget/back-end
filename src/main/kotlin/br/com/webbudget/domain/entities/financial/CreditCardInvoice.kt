package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.infrastructure.config.ApplicationSchemas.FINANCIAL
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
@Table(name = "credit_card_invoices", schema = FINANCIAL)
class CreditCardInvoice(
    @field:Column(name = "due_date", nullable = false)
    val dueDate: LocalDate,
    @field:Column(name = "total_value", nullable = false)
    val totalValue: BigDecimal,

    @field:ManyToOne
    @field:JoinColumn(name = "card_id", nullable = false)
    val card: Card,
    @field:ManyToOne
    @field:JoinColumn(name = "financial_period_id", nullable = false)
    val financialPeriod: FinancialPeriod,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "state", nullable = false, length = 9)
    var state: State = State.OPEN
) : PersistentEntity<Long>() {

    enum class State {
        OPEN, PAID, ACCOUNTED
    }
}