package br.com.webbudget.application.controllers

import br.com.webbudget.application.payloads.Credential
import br.com.webbudget.application.payloads.RefreshCredential
import br.com.webbudget.application.payloads.Token
import br.com.webbudget.domain.services.AuthenticationService
import br.com.webbudget.domain.services.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/authentication")
class AuthenticationController(
    private val tokenService: TokenService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid credential: Credential): ResponseEntity<Token> {

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(credential.username, credential.password)
        )

        val authenticable = authentication.principal as AuthenticationService.AuthenticableUser

        val token = tokenService.generateFor(authenticable.username)
        return ResponseEntity.ok(token)
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody refreshCredential: RefreshCredential): ResponseEntity<Token> {
        val token = tokenService.refresh(
            refreshCredential.tokenId.toString(),
            refreshCredential.username,
            refreshCredential.refreshToken
        )
        return ResponseEntity.ok(token)
    }
}
