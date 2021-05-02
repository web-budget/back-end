package br.com.webbudget.backend.services

import br.com.webbudget.backend.AbstractTest
import br.com.webbudget.backend.domain.services.TokenCacheService
import br.com.webbudget.backend.domain.services.TokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "web-budget.jwt.access-token-expiration=5",
        "web-budget.jwt.refresh-token-expiration=5"
    ]
)
class TokenServiceTest : AbstractTest() {

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var tokenCacheService: TokenCacheService

    @Test
    fun `should generate valid token`() {

        val token = tokenService.generateFrom("valid@webbudget.com.br")

        assertThat(token.accessToken).isNotBlank
        assertThat(token.refreshToken).isNotNull

        val subject = tokenService.extractSubject(token.accessToken)
        assertThat(subject).isEqualTo("valid@webbudget.com.br")
    }

    @Test
    fun `token should be put on cache`() {

        val subject = "cacheable@webbudget.com.br"

        tokenService.generateFrom(subject)

        val accessToken = tokenCacheService.findAccessToken(subject)
        assertThat(accessToken).isNotBlank

        val refreshToken = tokenCacheService.findRefreshToken(subject)
        assertThat(refreshToken).isNotNull
    }

    @Test
    fun `token should expire after 5 seconds`() {

        val token = tokenService.generateFrom("expire@webbudget.com.br")

        Thread.sleep(6000)

        val subject = tokenService.extractSubject(token.accessToken)

        val accessToken = tokenCacheService.findAccessToken(subject)
        assertThat(accessToken).isBlank

        val refreshToken = tokenCacheService.findRefreshToken(subject)
        assertThat(refreshToken).isNull()
    }
}