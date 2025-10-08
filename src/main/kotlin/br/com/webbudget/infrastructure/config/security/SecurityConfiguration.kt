package br.com.webbudget.infrastructure.config.security

import br.com.webbudget.domain.entities.administration.Role
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    @param:Value("\${web-budget.jwt.public-key}")
    private val publicKey: RSAPublicKey,
    @param:Value("\${web-budget.jwt.private-key}")
    private val privateKey: RSAPrivateKey,
    private val userRepository: UserRepository
) {

    @Bean
    fun configureSecurity(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/actuator/health/**", permitAll)
                authorize("/actuator/info/**", permitAll)
                authorize("/accounts/**", permitAll)
                authorize("/api/administration/**", hasRole(Role.ADMINISTRATION))
                authorize("/api/registration/**", hasRole(Role.REGISTRATION))
                authorize("/api/financial/**", hasRole(Role.FINANCIAL))
                authorize("/api/dashboards/**", hasRole(Role.DASHBOARDS))
                authorize("/api/investments/**", hasRole(Role.INVESTMENTS))
                authorize(anyRequest, authenticated)
            }
            httpBasic { }
            oauth2ResourceServer {
                jwt {
                    this.jwtAuthenticationConverter = jwtAuthenticationConverter()
                }
                bearerTokenResolver = cookieOrHeaderBearerTokenResolver()
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            exceptionHandling {
                authenticationEntryPoint = BearerTokenAuthenticationEntryPoint()
                accessDeniedHandler = BearerTokenAccessDeniedHandler()
            }
        }

        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(JWTScopeToRolesConverter())
        return converter
    }

    @Bean
    fun cookieOrHeaderBearerTokenResolver(): BearerTokenResolver = object : BearerTokenResolver {
        private val headerResolver = DefaultBearerTokenResolver()
        override fun resolve(request: HttpServletRequest): String? {
            val fromCookie = request.cookies?.firstOrNull { it.name == COOKIE_NAME }?.value
            return fromCookie ?: headerResolver.resolve(request)
        }
    }

    @Bean
    fun corsConfigurationSource(@Value("\${web-budget.frontend-url}") frontendUrl: String): CorsConfigurationSource {

        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(frontendUrl)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD")
        configuration.allowedHeaders = listOf("*")

        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun configureJwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build()

    @Bean
    fun configureJwtEncoder(): JwtEncoder {
        val jwk = RSAKey.Builder(publicKey).privateKey(privateKey).build()
        val jwkSource = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun configurePasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder(BCRYPT_STRENGTH)

    @Bean
    fun configureUserDetailsService(): UserDetailsService = UserDetailsService { username ->
        userRepository.findByEmail(username)
            ?.let { AuthenticableUser.of(it) }
            ?: throw UsernameNotFoundException("User [$username] not found")
    }

    companion object {
        private const val BCRYPT_STRENGTH = 11
        private const val COOKIE_NAME = "wb-auth"
    }
}