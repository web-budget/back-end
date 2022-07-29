package br.com.webbudget.infrastructure.config.spring

import br.com.webbudget.domain.services.configuration.AuthenticationService
import br.com.webbudget.domain.services.configuration.TokenService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component // FIXME review it when new auth process takes place
class TokenAuthenticationFilter(
    private val tokenService: TokenService,
    private val authenticationService: AuthenticationService
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

//        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
//
//        if (header.isNullOrBlank() || !header.startsWith("Bearer")) {
//            chain.doFilter(request, response)
//            return
//        }

//        val token = header.substring(TOKEN_START_INDEX, header.length)

//        if (tokenService.validate(token)) {

//            val username = tokenService.extractSubject(token)
//            val authenticable = authenticationService.loadUserByUsername(username)
            val authenticable = authenticationService.loadUserByUsername("admin@webbudget.com.br")

            val authentication = UsernamePasswordAuthenticationToken(
                authenticable, null, authenticable.authorities
            )

            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
//        }
        chain.doFilter(request, response)
    }

    companion object {
        private const val TOKEN_START_INDEX = 7
    }
}
