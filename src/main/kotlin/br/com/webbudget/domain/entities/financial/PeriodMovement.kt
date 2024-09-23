package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.FinancialPeriod
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
    @field:JoinColumn(name = "financial_period_id", nullable = false)
    var financialPeriod: FinancialPeriod,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "state", nullable = false, length = 9)
    var state: State = State.OPEN,

    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @field:OneToOne
    @field:JoinColumn(name = "payment_id")
    var payment: Payment? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "credit_card_invoice_id")
    var creditCardInvoice: CreditCardInvoice? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "recurring_movement_id")
    var recurringMovement: RecurringMovement? = null
) : PersistentEntity<Long>() {

    enum class State {
        OPEN, PAID, ACCOUNTED
    }
}