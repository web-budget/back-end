package br.com.webbudget.backend.application.payloads

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class Credential(
    @field:Email
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)

data class RefreshCredential(
    @field:Email
    @field:NotBlank
    val username: String,
    @field:NotNull
    val refreshToken: UUID
)

data class Token(
    @field:NotBlank
    val accessToken: String,
    @field:NotBlank
    val refreshToken: UUID,
    @field:NotNull
    val expireIn: Long
)