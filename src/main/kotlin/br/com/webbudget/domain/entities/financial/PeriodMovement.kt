package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.infrastructure.config.ApplicationSchemas.FINANCIAL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "period_movements", schema = FINANCIAL)
class PeriodMovement(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "due_date", nullable = false)
    var dueDate: LocalDate,
    @field:Column(name = "value", nullable = false)
    var value: BigDecimal,

    @field:ManyToOne
    @field:JoinColumn(name = "id_classification", nullable = false)
    var classification: Classification,
    @field:ManyToOne
    @field:JoinColumn(name = "id_financial_period", nullable = false)
    var financialPeriod: FinancialPeriod,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "state", nullable = false, length = 9)
    var state: State = State.OPEN,

    @field:Column(name = "quote_number", length = 3)
    var quoteNumber: Int? = null,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @field:OneToOne
    @field:JoinColumn(name = "id_payment")
    var payment: Payment? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "id_credit_card_invoice")
    var creditCardInvoice: CreditCardInvoice? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "id_recurring_movement")
    var recurringMovement: RecurringMovement? = null,
) : PersistentEntity<Long>() {

    fun isOpen(): Boolean = state == State.OPEN

    fun isAccounted(): Boolean = state == State.ACCOUNTED

    enum class State {
        OPEN, PAID, ACCOUNTED
    }
}