package br.com.webbudget.application.payloads

enum class StatusFilter(val value: Boolean?) {
    ALL(null), ACTIVE(true), INACTIVE(false)
}
