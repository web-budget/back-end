package br.com.webbudget.backend.domain.services

import br.com.webbudget.backend.application.payloads.Token
import br.com.webbudget.backend.domain.exceptions.BadRefreshTokenException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class TokenService(
    @Value("\${web-budget.jwt.secret}")
    private val jwtSecret: String,
    @Value("\${web-budget.jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,
    private val tokenCacheService: TokenCacheService
) {

    fun generateFrom(subject: String): Token {

        val token = JWT.create()
            .withSubject(subject)
            .withIssuedAt(Date())
            .withIssuer("br.com.webbudget")
            .withExpiresAt(Date.from(Instant.now().plusSeconds(accessTokenExpiration)))
            .sign(Algorithm.HMAC512(jwtSecret))

        val refreshToken = UUID.randomUUID()

        tokenCacheService.remove(subject)

        tokenCacheService.storeAccessToken(subject, token)
        tokenCacheService.storeRefreshToken(subject, refreshToken)

        return Token(token, refreshToken, accessTokenExpiration)
    }

    fun refresh(subject: String, refreshToken: UUID): Token {

        val storedRefreshToken = tokenCacheService.findRefreshToken(subject)

        if (storedRefreshToken == null && storedRefreshToken != refreshToken) {
            throw BadRefreshTokenException("Refresh token [$refreshToken] is invalid")
        }

        return generateFrom(subject)
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

