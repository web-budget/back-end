package br.com.webbudget.backend.services

import br.com.webbudget.backend.AbstractTest
import br.com.webbudget.backend.domain.services.TokenCacheService
import br.com.webbudget.backend.domain.services.TokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TokenServiceTest : AbstractTest() {

    @Autowired
    private lateinit var tokenService: TokenService
    @Autowired
    private lateinit var tokenCacheService: TokenCacheService

    @Test
    fun `should generate valid token`() {

        val token = tokenService.generateFrom("someone@webbudget.com.br")
        val subject = tokenService.extractSubject(token.accessToken)

        assertThat(token.accessToken).isNotBlank
        assertThat(subject).isEqualTo("someone@webbudget.com.br")
    }

    @Test
    fun `should cache token`() {

        val token = tokenService.generateFrom("someone@webbudget.com.br")
        val subject = tokenService.extractSubject(token.accessToken)

        val found = tokenCacheService.find(subject)

        assertThat(found).isNotBlank
    }
}