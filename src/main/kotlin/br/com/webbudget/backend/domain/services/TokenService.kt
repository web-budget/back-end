package br.com.webbudget.backend.domain.services

import br.com.webbudget.backend.application.payloads.Token
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

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
        val instantOfExpiration = now.plusSeconds(this.secondsToExpire)

        val token = Jwts.builder()
            .setSubject(subject)
            .setIssuer("br.com.web-budget")
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(instantOfExpiration))
            .signWith(this.jwtSecretAsSecretKey(), SignatureAlgorithm.HS512)
            .compact()

        this.tokenCacheService.store(subject, token)

        return Token(token, "", LocalDateTime.from(instantOfExpiration))
    }

    fun validate(token: String): ValidationResult {
        return try {
            this.parseAndClaim(token)

            val subject = this.extract(Claims.SUBJECT, token)
            if (this.tokenCacheService.isExpired(subject)) {
                return ValidationResult.EXPIRED
            }

            ValidationResult.VALID
        } catch (ex: ExpiredJwtException) {
            return ValidationResult.EXPIRED
        } catch (ex: Exception) {
            return ValidationResult.INVALID
        }
    }

    fun extract(key: String, token: String): String {
        return this.parseAndClaim(token).body.get(key, String::class.java)
    }

    private fun parseAndClaim(token: String): Jws<Claims> {
        val parser = Jwts.parserBuilder()
            .setSigningKey(this.jwtSecretAsSecretKey())
            .build()
        return parser.parseClaimsJws(token)
    }

    private fun jwtSecretAsSecretKey(): SecretKey {
        return Keys.hmacShaKeyFor(this.jwtSecret.toByteArray(Charsets.UTF_8))
    }

    enum class ValidationResult {

        VALID, INVALID, EXPIRED;

        fun isValid(): Boolean {
            return this == VALID
        }
    }
}

