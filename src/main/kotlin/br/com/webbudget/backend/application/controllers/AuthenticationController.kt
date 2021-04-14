package br.com.webbudget.backend.application.controllers

import br.com.webbudget.backend.application.payloads.authentication.Credential
import br.com.webbudget.backend.application.payloads.authentication.Token
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/authentication")
class AuthenticationController {

    @PostMapping
    fun authenticate(@RequestBody credential: Credential) : ResponseEntity<Token> {
        return ResponseEntity.ok().build()
    }
}