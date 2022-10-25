package br.com.webbudget.application.controllers

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
    fun activateAccount(@RequestBody activationKey: String): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/recover-password")
    fun recoverPassword(@RequestBody email: String): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }
}
