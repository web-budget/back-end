package br.com.webbudget.application.controllers

import br.com.webbudget.application.payloads.ActivateAccountForm
import br.com.webbudget.application.payloads.ForgotPasswordForm
import br.com.webbudget.application.payloads.RecoverPasswordForm
import br.com.webbudget.domain.services.administration.AccountActivationService
import br.com.webbudget.domain.services.administration.RecoverPasswordService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-account")
class UserAccountController(
    private val recoverPasswordService: RecoverPasswordService,
    private val accountActivationService: AccountActivationService
) {

    @PatchMapping("/activate")
    fun activateAccount(@RequestBody @Valid form: ActivateAccountForm): ResponseEntity<Any> {

        val token = requireNotNull(form.token)
        val email = requireNotNull(form.email)

        accountActivationService.activate(token, email)

        return ResponseEntity.ok().build()
    }

    @PatchMapping("/forgot-password")
    fun forgotPassword(@RequestBody @Valid form: ForgotPasswordForm): ResponseEntity<Any> {
        recoverPasswordService.registerRecoveryAttempt(form.email!!)
        return ResponseEntity.accepted().build()
    }

    @PatchMapping("/recover-password")
    fun recoverPassword(@RequestBody @Valid form: RecoverPasswordForm): ResponseEntity<Any> {

        val token = requireNotNull(form.token)
        val userEmail = requireNotNull(form.email)
        val newPassword = requireNotNull(form.password)

        recoverPasswordService.recover(newPassword, token, userEmail)

        return ResponseEntity.ok().build()
    }
}
