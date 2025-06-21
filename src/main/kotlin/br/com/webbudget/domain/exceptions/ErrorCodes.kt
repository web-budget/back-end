package br.com.webbudget.domain.exceptions

object ErrorCodes {
    const val INVALID_TOKEN = "invalid-token"
    const val ACCOUNTED_PERIOD_MOVEMENT = "accounted-period-movement"
    const val INVALID_APPORTIONMENTS = "invalid-apportionments"
    const val FINANCIAL_PERIOD_NOT_OPEN = "financial-period-not-open"
    const val BUDGET_LIMIT_EXCEEDED = "budget-limit-exceeded"
    const val INVALID_INVOICE_PAYMENT_DAY = "invalid-invoice-payment-day"
    const val DEBIT_CARD_WITHOUT_WALLET = "debit-card-without-wallet"
    const val START_DATE_AFTER_END_DATE = "start-date-after-end-date"
    const val OVERLAPPING_START_END_DATES = "overlapping-start-end-dates"
    const val FIELD_VALIDATION_FAILED = "field-validation-failed"
}