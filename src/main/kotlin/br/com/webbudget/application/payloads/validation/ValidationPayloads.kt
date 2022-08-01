package br.com.webbudget.application.payloads.validation

data class ValidationError(val violations: List<Violation>)

data class Violation(val property: String, val message: String?)
