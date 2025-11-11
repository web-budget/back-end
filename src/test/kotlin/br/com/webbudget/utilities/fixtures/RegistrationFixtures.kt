package br.com.webbudget.utilities.fixtures

import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.entities.registration.Wallet
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

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

fun createCostCenter(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "Cost Center",
    active: Boolean = true,
    description: String = "Some description",
    incomeBudget: BigDecimal? = BigDecimal.ONE,
    expenseBudget: BigDecimal? = BigDecimal.ONE,
) = CostCenter(name, active, description, incomeBudget, expenseBudget)
    .apply {
        this.id = id
        this.externalId = externalId
    }

fun createMovementClass(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "Movement Class",
    type: MovementClass.Type = MovementClass.Type.INCOME,
    active: Boolean = true,
    costCenter: CostCenter = createCostCenter(),
    budget: BigDecimal? = BigDecimal.ONE,
    description: String = "Some description"
) = MovementClass(name, type, active, costCenter, budget, description)
    .apply {
        this.id = id
        this.externalId = externalId
    }

fun createCard(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "Card",
    lastFourDigits: String = "1234",
    invoicePaymentDay: Int? = 1,
    type: Card.Type = Card.Type.CREDIT,
    active: Boolean = true,
    flag: String = "Flag",
    wallet: Wallet? = null
) = Card(name, lastFourDigits, type, active, invoicePaymentDay, flag, wallet)
    .apply {
        this.id = id
        this.externalId = externalId
    }

fun createFinancialPeriod(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    name: String = "Financial Period",
    startingAt: LocalDate = LocalDate.now(),
    endingAt: LocalDate = LocalDate.now().plusDays(15),
    status: FinancialPeriod.Status = FinancialPeriod.Status.ACTIVE,
    expensesGoal: BigDecimal = BigDecimal.ONE,
    revenuesGoal: BigDecimal = BigDecimal.TEN,
) = FinancialPeriod(name, startingAt, endingAt, status, expensesGoal, revenuesGoal)
    .apply {
        this.id = id
        this.externalId = externalId
    }