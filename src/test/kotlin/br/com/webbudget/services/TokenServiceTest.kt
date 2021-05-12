package br.com.webbudget.services

import br.com.webbudget.AbstractTest
import br.com.webbudget.domain.services.CacheService
import br.com.webbudget.domain.services.TokenService
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
    private lateinit var cacheService: CacheService

    @Test
    fun `should generate token`() {

        val token = tokenService.generateFor("valid@webbudget.com.br")

        assertThat(token.id).isNotNull
        assertThat(token.accessToken).isNotBlank
        assertThat(token.refreshToken).isNotNull

        val subject = tokenService.extractSubject(token.accessToken)
        assertThat(subject).isEqualTo("valid@webbudget.com.br")
    }

    @Test
    fun `should put generated token on cache`() {

        val token = tokenService.generateFor("cacheable@webbudget.com.br")

        val accessToken = cacheService.find("access_token:${token.id}") as String
        assertThat(accessToken).isNotBlank

        val refreshToken = cacheService.find("refresh_token:${token.id}") as String
        assertThat(refreshToken).isNotNull
    }

    @Test
    fun `access token should expire after 5 seconds`() {

        val token = tokenService.generateFor("expire@webbudget.com.br")

        Thread.sleep(6000)

        val accessToken = cacheService.find("access_token:${token.id}") as? String
        assertThat(accessToken).isBlank
    }

    @Test
    fun `refresh token should expire after 5 seconds`() {

        val token = tokenService.generateFor("expire@webbudget.com.br")

        Thread.sleep(6000)

        val refreshToken = cacheService.find("refresh_token:${token.id}") as? String
        assertThat(refreshToken).isBlank
    }
}