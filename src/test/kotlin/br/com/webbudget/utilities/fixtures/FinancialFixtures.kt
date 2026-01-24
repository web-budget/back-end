package br.com.webbudget.utilities.fixtures

import br.com.webbudget.domain.entities.financial.CreditCardInvoice
import br.com.webbudget.domain.entities.financial.Payment
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.entities.registration.FinancialPeriod
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
    classification: Classification = createClassification(),
    state: PeriodMovement.State = PeriodMovement.State.OPEN,
    quoteNumber: Int? = null,
    description: String? = null,
    payment: Payment? = null,
    creditCardInvoice: CreditCardInvoice? = null,
    recurringMovement: RecurringMovement? = null
): PeriodMovement = PeriodMovement(
    name = name,
    dueDate = dueDate,
    value = value,
    classification = classification,
    financialPeriod = financialPeriod,
    state = state,
    quoteNumber = quoteNumber,
    description = description,
    payment = payment,
    creditCardInvoice = creditCardInvoice,
    recurringMovement = recurringMovement
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
    classification: Classification = createClassification(),
    autoLaunch: Boolean = true,
    indeterminate: Boolean = true,
    totalQuotes: Int? = null,
    startingQuote: Int? = null,
    currentQuote: Int? = null,
    description: String? = null,
): RecurringMovement = RecurringMovement(
    name = name,
    value = value,
    startingAt = startingAt,
    classification = classification,
    state = state,
    autoLaunch = autoLaunch,
    indeterminate = indeterminate,
    totalQuotes = totalQuotes,
    startingQuote = startingQuote,
    currentQuote = currentQuote,
    description = description
).apply {
    this.id = id
    this.externalId = externalId
}