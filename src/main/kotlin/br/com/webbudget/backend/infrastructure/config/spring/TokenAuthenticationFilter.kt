package br.com.webbudget.backend.infrastructure.config.spring

import br.com.webbudget.backend.domain.services.AuthenticationService
import br.com.webbudget.backend.domain.services.TokenService
import io.jsonwebtoken.Claims
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenAuthenticationFilter(
    private val tokenService: TokenService,
    private val authenticationService: AuthenticationService
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (header.isNullOrBlank() || !header.startsWith("Bearer")) {
            chain.doFilter(request, response)
            return
        }

        val token = header.substring(7, header.length)
        val result = this.tokenService.validate(token)

        if (result.isValid()) {

            val username = this.tokenService.extract(Claims.SUBJECT, token)
            val authenticable = this.authenticationService.loadUserByUsername(username)

            val authentication = UsernamePasswordAuthenticationToken(
                authenticable, null, authenticable.authorities
            )

            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }
}