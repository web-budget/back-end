package br.com.webbudget.domain.services.administration

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class TokenService(
    private val jwtEncoder: JwtEncoder,
    @Value("\${web-budget.jwt.access-token-expiration:2400}")
    private val accessTokenExpiration: Long
) {

    fun generateFor(subject: String, scope: List<String>): String {

        val now = Instant.now()
        val tokenId = UUID.randomUUID()

        val claims = JwtClaimsSet.builder()
            .issuer(TOKEN_ISSUER)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(accessTokenExpiration))
            .subject(subject)
            .claim(SCOPE_CLAIM, scope)
            .id(tokenId.toString())
            .build()

        val jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))

        return jwt.tokenValue
    }

    companion object {
        private const val SCOPE_CLAIM = "scope"
        private const val TOKEN_ISSUER = "br.com.webbudget"
    }
}
