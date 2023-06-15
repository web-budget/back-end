package br.com.webbudget.application.payloads

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class ActivateAccountForm(
    @field:NotNull(message = "account-activation.errors.invalid-token")
    val token: UUID?,
    @field:Email
    @field:NotBlank(message = "account-activation.errors.invalid-email")
    val email: String?
)

data class ForgotPasswordForm(
    @field:Email
    @field:NotBlank(message = "forgot-password.errors.invalid-email")
    val email: String?
)

data class RecoverPasswordForm(
    @field:NotNull(message = "recover-password.errors.invalid-token")
    val token: UUID?,
    @field:Email
    @field:NotBlank(message = "recover-password.errors.invalid-email")
    val email: String?,
    @field:NotBlank(message = "recover-password.errors.null-password")
    val password: String?
)
