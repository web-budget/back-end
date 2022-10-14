package br.com.webbudget.infrastructure.config.spring

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
class SecurityConfiguration(
    private val publicKey: RSAPublicKey = KeyPairGenerator.getPublicKey(),
    private val privateKey: RSAPrivateKey = KeyPairGenerator.getPrivateKey()
) {

    @Bean
    fun configureSecurity(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }
            csrf { disable() }
            authorizeRequests {
                authorize("/actuator/health/**", permitAll)
                authorize("/api/administration/**", hasAuthority("SCOPE_ADMINISTRATION"))
                authorize("/api/registration/**", hasAuthority("SCOPE_REGISTRATION"))
                authorize("/api/financial/**", hasAuthority("SCOPE_FINANCIAL"))
                authorize("/api/dashboards/**", hasAuthority("SCOPE_DASHBOARDS"))
                authorize(anyRequest, authenticated)
            }
            httpBasic { }
            oauth2ResourceServer { jwt { } }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            exceptionHandling {
                authenticationEntryPoint = BearerTokenAuthenticationEntryPoint()
                accessDeniedHandler = BearerTokenAccessDeniedHandler()
            }
        }

        return http.build()
    }

    @Bean
    fun configureJwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(publicKey).build()
    }

    @Bean
    fun configureJwtEncoder(): JwtEncoder {
        val jwk = RSAKey.Builder(publicKey).privateKey(privateKey).build()
        val jwkSource = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun configurePasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(BCRYPT_STRENGTH)
    }

    companion object {
        private const val BCRYPT_STRENGTH = 11
    }
}
