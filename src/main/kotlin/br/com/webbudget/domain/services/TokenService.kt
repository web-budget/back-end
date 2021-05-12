package br.com.webbudget.domain.services

import br.com.webbudget.application.payloads.Token
import br.com.webbudget.domain.exceptions.BadRefreshTokenException
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
    private val cacheService: CacheService,
    @Value("\${web-budget.jwt.secret}")
    private val jwtSecret: String,
    @Value("\${web-budget.jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,
    @Value("\${web-budget.jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long
) {

    fun generateFor(subject: String): Token {

        val tokenId = UUID.randomUUID()

        val accessToken = JWT.create()
            .withJWTId(tokenId.toString())
            .withSubject(subject)
            .withIssuedAt(Date())
            .withIssuer(TOKEN_ISSUER)
            .withExpiresAt(Date.from(Instant.now().plusSeconds(accessTokenExpiration)))
            .sign(Algorithm.HMAC512(jwtSecret))

        val token = Token(tokenId, accessToken, UUID.randomUUID(), accessTokenExpiration)

        cacheService.store(accessTokenKey(tokenId.toString()), token.accessToken, accessTokenExpiration)
        cacheService.store(refreshTokenKey(tokenId.toString()), token.refreshToken, refreshTokenExpiration)

        return token
    }

    fun refresh(tokenId: String, subject: String, refreshToken: UUID): Token {

        val actualRefreshToken = cacheService.find(refreshTokenKey(tokenId)) as String

        if (actualRefreshToken.isBlank() || UUID.fromString(actualRefreshToken) != refreshToken) {
            throw BadRefreshTokenException("Refresh token [$refreshToken] is invalid")
        }

        cacheService.remove(accessTokenKey(tokenId))
        cacheService.remove(refreshTokenKey(tokenId))

        return generateFor(subject)
    }

    fun validate(accessToken: String): Boolean {
        return try {
            val verifier = JWT.require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(TOKEN_ISSUER)
                .build()

            val decoded = verifier.verify(accessToken)
            if (cacheService.isExpired(accessTokenKey(decoded.id))) {
                return false
            }

            true
        } catch (ex: JWTVerificationException) {
            false
        }
    }

    fun extractSubject(accessToken: String): String {
        val decoded = JWT.decode(accessToken)
        return decoded.subject
    }

    fun extractId(accessToken: String): String {
        val decoded = JWT.decode(accessToken)
        return decoded.id
    }

    private fun accessTokenKey(tokenId: String): String = ACCESS_TOKEN_KEY + tokenId

    private fun refreshTokenKey(tokenId: String): String = REFRESH_TOKEN_KEY + tokenId

    companion object {
        private const val TOKEN_ISSUER = "br.com.webbudget"
        private const val ACCESS_TOKEN_KEY = "access_token:"
        private const val REFRESH_TOKEN_KEY = "refresh_token:"
    }
}

