package br.com.webbudget.backend.application.payloads.authentication

data class Token(
    val token: String,
    val refreshToken: String,
)