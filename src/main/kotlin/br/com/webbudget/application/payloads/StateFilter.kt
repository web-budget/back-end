package br.com.webbudget.application.payloads

enum class StateFilter(val value: Boolean?) {
    ALL(null), ACTIVE(true), INACTIVE(false)
}
