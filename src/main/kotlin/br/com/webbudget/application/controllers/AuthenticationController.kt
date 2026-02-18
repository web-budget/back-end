package br.com.webbudget.application.controllers

import br.com.webbudget.application.payloads.ProfileView
import br.com.webbudget.domain.services.administration.TokenService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val tokenService: TokenService,
    private val userRepository: UserRepository
) {

    @PostMapping("/login")
    fun login(authentication: Authentication, response: HttpServletResponse): ResponseEntity<Void> {

        val username = authentication.name

        val grantedAuthorities = authentication.authorities
            .map { it.authority!! }
            .toList()

        val jwt = tokenService.generate(username, grantedAuthorities)

        response.setHeader(HttpHeaders.SET_COOKIE, buildCookie(jwt, Duration.ofHours(1)).toString())

        return ResponseEntity.ok().build()
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        response.setHeader(HttpHeaders.SET_COOKIE, buildCookie("", Duration.ofSeconds(0)).toString())
        return ResponseEntity.ok().build()
    }

    @GetMapping("/me")
    fun me(authentication: Authentication): ResponseEntity<ProfileView> {

        val username = authentication.name

        val user = userRepository.findByEmail(username)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(ProfileView(user.name, user.email))
    }

    private fun buildCookie(jwt: String, duration: Duration): ResponseCookie = ResponseCookie.from(COOKIE_NAME, jwt)
        .httpOnly(true)
        .secure(true)
        .sameSite("Lax")
        .path("/")
        .maxAge(duration)
        .build()

    companion object {
        private const val COOKIE_NAME = "wb-auth"
    }
}