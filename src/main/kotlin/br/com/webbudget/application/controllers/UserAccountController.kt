package br.com.webbudget.application.controllers

import br.com.webbudget.application.payloads.ActivateAccountForm
import br.com.webbudget.application.payloads.ForgotPasswordForm
import br.com.webbudget.application.payloads.RecoverPasswordForm
import br.com.webbudget.domain.services.administration.UserAccountService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("UnusedPrivateMember")

@RestController
@RequestMapping("/user-account")
class UserAccountController(
    private val userAccountService: UserAccountService
) {

    @PatchMapping("/activate")
    fun activateAccount(@RequestBody form: ActivateAccountForm): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/forgot-password")
    fun forgotPassword(@RequestBody form: ForgotPasswordForm): ResponseEntity<Any> {
        userAccountService.recoverPassword(form.email!!)
        return ResponseEntity.accepted().build()
    }

    @PatchMapping("/recover-password")
    fun recoverPassword(@RequestBody form: RecoverPasswordForm): ResponseEntity<Any> {
        println(form)
        return ResponseEntity.ok().build()
    }
}
