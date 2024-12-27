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
    @Value("\${web-budget.jwt.expiration-seconds}")
    private val expirationSeconds: Long
) {

    fun generate(subject: String, scope: List<String>): String {

        val claims = JwtClaimsSet.builder()
            .id(UUID.randomUUID().toString())
            .issuer(ISSUER)
            .subject(subject)
            .claim(SCOPE_CLAIM, scope)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(expirationSeconds))
            .build()

        val jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))

        return jwt.tokenValue
    }

    companion object {
        private const val SCOPE_CLAIM = "scope"
        private const val ISSUER = "br.com.webbudget"
    }
}
