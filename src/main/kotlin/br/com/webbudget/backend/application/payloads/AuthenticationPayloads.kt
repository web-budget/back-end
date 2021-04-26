package br.com.webbudget.backend.application.payloads

import java.time.LocalDateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class Credential(
    @field:Email
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val validity: LocalDateTime
)