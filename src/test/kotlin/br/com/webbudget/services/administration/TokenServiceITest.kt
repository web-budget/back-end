package br.com.webbudget.services.administration

import br.com.webbudget.domain.services.administration.TokenService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import java.time.Instant
import java.util.UUID

@ExtendWith(MockKExtension::class)
class TokenServiceITest {

    @MockK
    private lateinit var jwtEncoder: JwtEncoder

    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setup() {
        this.tokenService = TokenService(jwtEncoder, 1)
    }

    @Test
    fun `should generate authentication token`() {

        val now = Instant.now()

        val headers = mapOf("some" to "header")
        val claims = mapOf("some" to "claim")

        val jwt = Jwt(UUID.randomUUID().toString(), now, now.plusSeconds(60), headers, claims)

        every { jwtEncoder.encode(any()) } returns jwt

        val token = tokenService.generateFor("someone", listOf("ADMINISTRATION"))
        assertThat(token).isNotBlank
    }
}
