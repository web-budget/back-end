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

fun createPeriodMovement(
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
    apportionments: List<Apportionment> = listOf(createApportionment()),
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
)

fun createApportionment(
    value: BigDecimal = BigDecimal.ONE,
    movementClass: MovementClass = createMovementClass(),
    periodMovement: PeriodMovement? = null,
    recurringMovement: RecurringMovement? = null
): Apportionment = Apportionment(value, movementClass, periodMovement, recurringMovement)