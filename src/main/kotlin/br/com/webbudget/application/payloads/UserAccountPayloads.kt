package br.com.webbudget.application.payloads

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class ActivateAccountForm(
    @field:NotNull(message = "is-null")
    val token: UUID?,
    @field:Email(message = "is-not-valid")
    @field:NotBlank(message = "is-null-or-blank")
    val email: String?
)

data class ForgotPasswordForm(
    @field:Email(message = "is-not-valid")
    @field:NotBlank(message = "is-null-or-blank")
    val email: String?
)

data class RecoverPasswordForm(
    @field:NotNull(message = "is-null")
    val token: UUID?,
    @field:Email(message = "is-not-valid")
    @field:NotBlank(message = "is-null-or-blank")
    val email: String?,
    @field:NotBlank(message = "is-null-or-blank")
    val password: String?
)
