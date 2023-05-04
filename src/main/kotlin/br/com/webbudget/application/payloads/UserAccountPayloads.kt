package br.com.webbudget.application.payloads

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class ActivateAccountForm(
    @NotNull(message = "account-activation.errors.invalid-token")
    val token: UUID?
)

data class ForgotPasswordForm(
    @Email
    @NotBlank(message = "forgot-password.errors.invalid-email")
    val email: String?
)

data class RecoverPasswordForm(
    @NotNull(message = "recover-password.errors.invalid-token")
    val token: UUID?,
    @Email
    @NotBlank(message = "recover-password.errors.invalid-email")
    val email: String?,
    @NotBlank(message = "recover-password.errors.null-password")
    val password: String?
)
