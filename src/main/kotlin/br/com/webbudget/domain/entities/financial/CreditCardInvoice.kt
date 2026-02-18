package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.FinancialPeriod
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
@Table(name = "credit_card_invoices", schema = FINANCIAL)
class CreditCardInvoice(
    @field:Column(name = "due_date", nullable = false)
    val dueDate: LocalDate,
    @field:Column(name = "value", nullable = false)
    val value: BigDecimal,

    @field:ManyToOne
    @field:JoinColumn(name = "id_card", nullable = false)
    val card: Card,
    @field:ManyToOne
    @field:JoinColumn(name = "id_financial_period", nullable = false)
    val financialPeriod: FinancialPeriod,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "state", nullable = false, length = 9)
    var state: State = State.OPEN
) : PersistentEntity<Long>() {

    enum class State {
        OPEN, PAID, ACCOUNTED
    }
}