package br.com.webbudget.application.payloads

object ErrorCodes {
    const val IS_NULL = "is-null"
    const val IS_EMPTY = "is-empty"
    const val IS_NULL_OR_BLANK = "is-null-or-blank"
    const val MAX_CHARS = "has-max-chars-of-{max}"
    const val EMAIL_IS_INVALID = "email-address-is-invalid"
}