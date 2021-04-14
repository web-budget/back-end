package br.com.webbudget.backend.application.payloads.authentication

data class Credential(
    val username: String,
    val password: String
)