package br.com.webbudget.backend.domain.services

import br.com.webbudget.backend.application.payloads.Token
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Service
class TokenService(
    @Value("\${web-budget.jwt.secret}")
    private val jwtSecret: String,
    @Value("\${web-budget.jwt.seconds-to-expire}")
    private val secondsToExpire: Long,
    private val tokenCacheService: TokenCacheService
) {

    fun generateFrom(subject: String): Token {

        val now = Instant.now()
        val instantOfExpiration = now.plusSeconds(secondsToExpire)

        val token = JWT.create()
            .withSubject(subject)
            .withIssuer("br.com.webbudget")
            .withExpiresAt(Date.from(instantOfExpiration))
            .sign(Algorithm.HMAC512(jwtSecret))

        tokenCacheService.store(subject, token)

        return Token(token, "", LocalDateTime.ofInstant(instantOfExpiration, ZoneId.systemDefault()))
    }

    fun validate(token: String): Boolean {
        return try {
            val verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer("br.com.webbudget")
                .build()

            val decoded = verifier.verify(token)
            if (tokenCacheService.isExpired(decoded.subject)) {
                return false
            }

            true
        } catch (ex: JWTVerificationException) {
            false
        }
    }

    fun extractSubject(token: String): String {
        val decoded = JWT.decode(token)
        return decoded.subject
    }
}

