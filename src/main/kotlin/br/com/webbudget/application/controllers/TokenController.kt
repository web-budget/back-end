package br.com.webbudget.application.controllers

import br.com.webbudget.domain.services.administration.TokenService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/token")
class TokenController(
    private val tokenService: TokenService,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun create(authentication: Authentication): ResponseEntity<TokenResponse> {

        val username = authentication.name
        val authenticatedUser = userRepository.findByEmail(username) ?: throw UsernameNotFoundException(username)

        val grantedAuthorities = authentication.authorities
            .map { it.authority }
            .toList()

        val token = tokenService.generateFor(username, grantedAuthorities)

        return ResponseEntity.ok(TokenResponse(authenticatedUser.name, authenticatedUser.email, token))
    }

    data class TokenResponse(
        val name: String,
        val email: String,
        val token: String,
    )
}
