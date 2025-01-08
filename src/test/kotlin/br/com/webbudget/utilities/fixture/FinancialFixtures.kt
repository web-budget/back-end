package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.financial.Apportionment
import br.com.webbudget.domain.entities.financial.CreditCardInvoice
import br.com.webbudget.domain.entities.financial.Payment
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.MovementClass
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

fun createPeriodMovement(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "The movement",
    dueDate: LocalDate = LocalDate.now(),
    value: BigDecimal = BigDecimal.ONE,
    financialPeriod: FinancialPeriod = createFinancialPeriod(),
    state: PeriodMovement.State = PeriodMovement.State.OPEN,
    quoteNumber: Int? = null,
    description: String? = null,
    payment: Payment? = null,
    creditCardInvoice: CreditCardInvoice? = null,
    recurringMovement: RecurringMovement? = null,
    apportionments: MutableList<Apportionment> = mutableListOf(createApportionment()),
): PeriodMovement = PeriodMovement(
    name,
    dueDate,
    value,
    financialPeriod,
    state,
    quoteNumber,
    description,
    payment,
    creditCardInvoice,
    recurringMovement,
    apportionments
).apply {
    this.id = id
    this.externalId = externalId
}

fun createRecurringMovement(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "The movement",
    value: BigDecimal = BigDecimal.ONE,
    startingAt: LocalDate = LocalDate.now(),
    state: RecurringMovement.State = RecurringMovement.State.ACTIVE,
    autoLaunch: Boolean = true,
    indeterminate: Boolean = true,
    totalQuotes: Int? = null,
    startingQuote: Int? = null,
    currentQuote: Int? = null,
    description: String? = null,
    apportionments: MutableList<Apportionment> = mutableListOf(createApportionment()),
): RecurringMovement = RecurringMovement(
    name,
    value,
    startingAt,
    state,
    autoLaunch,
    indeterminate,
    totalQuotes,
    startingQuote,
    currentQuote,
    description,
    apportionments
).apply {
    this.id = id
    this.externalId = externalId
}

fun createApportionment(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    value: BigDecimal = BigDecimal.ONE,
    movementClass: MovementClass = createMovementClass(),
    periodMovement: PeriodMovement? = null,
    recurringMovement: RecurringMovement? = null
): Apportionment = Apportionment(value, movementClass, periodMovement, recurringMovement)
    .apply {
        this.id = id
        this.externalId = externalId
    }