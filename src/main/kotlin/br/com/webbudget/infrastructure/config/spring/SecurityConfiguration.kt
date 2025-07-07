package br.com.webbudget.infrastructure.config.spring

import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import com.google.common.collect.ImmutableList
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration(
    @Value("\${web-budget.jwt.public-key}")
    private val publicKey: RSAPublicKey,
    @Value("\${web-budget.jwt.private-key}")
    private val privateKey: RSAPrivateKey,
    private val userRepository: UserRepository
) {

    @Bean
    fun configureSecurity(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {  }
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/actuator/health/**", permitAll)
                authorize("/actuator/info/**", permitAll)
                authorize("/accounts/**", permitAll)
                authorize("/api/administration/**", hasAuthority(Authority.ADMINISTRATION))
                authorize("/api/registration/**", hasAuthority(Authority.REGISTRATION))
                authorize("/api/financial/**", hasAuthority(Authority.FINANCIAL))
                authorize("/api/dashboards/**", hasAuthority(Authority.DASHBOARDS))
                authorize("/api/investments/**", hasAuthority(Authority.INVESTMENTS))
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
    fun corsConfigurationSource(@Value("\${web-budget.frontend-url}") frontendUrl: String): CorsConfigurationSource {

        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(frontendUrl)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD")
        configuration.allowedHeaders = listOf("*")

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

    data class AuthenticableUser(
        private val username: String,
        private val password: String,
        private val active: Boolean,
        private val authorities: List<GrantedAuthority>
    ) : UserDetails {

        override fun getAuthorities(): List<GrantedAuthority> = ImmutableList.copyOf(authorities)

        override fun getUsername(): String = username

        override fun getPassword(): String = password

        override fun isAccountNonExpired(): Boolean = active

        override fun isAccountNonLocked(): Boolean = active

        override fun isCredentialsNonExpired(): Boolean = active

        override fun isEnabled(): Boolean = active

        companion object {
            fun of(user: User): AuthenticableUser = user.grants
                .map { grant -> grant.authority }
                .map { authority -> SimpleGrantedAuthority(authority.name) }
                .let { AuthenticableUser(user.email, user.password, user.active, it) }
        }
    }

    companion object {
        private const val BCRYPT_STRENGTH = 11
    }
}
