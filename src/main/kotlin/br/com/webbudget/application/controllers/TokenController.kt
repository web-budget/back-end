package br.com.webbudget.application.controllers

import br.com.webbudget.domain.services.configuration.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/token")
class TokenController(
    private val tokenService: TokenService
) {

    @PostMapping
    fun create(authentication: Authentication): ResponseEntity<TokenResponse> {

        val grantedAuthorities = authentication.authorities
            .map { it.authority }
            .toList()

        val token = tokenService.generateFor(authentication.name, grantedAuthorities)

        return ResponseEntity.ok(TokenResponse(token))
    }

    data class TokenResponse(
        val token: String
    )
}
