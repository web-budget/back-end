package br.com.webbudget.utilities

import br.com.webbudget.domain.entities.administration.Authority
import org.springframework.security.core.authority.SimpleGrantedAuthority

object Authorities {

    val ADMINISTRATION = SimpleGrantedAuthority("SCOPE_ADMINISTRATION")
    val FINANCIAL = SimpleGrantedAuthority("SCOPE_FINANCIAL")
    val REGISTRATION = SimpleGrantedAuthority("SCOPE_REGISTRATION")
    val DASHBOARDS = SimpleGrantedAuthority("SCOPE_DASHBOARDS")

    fun asList(): List<Authority> {
        return listOf(
            Authority("ADMINISTRATION"),
            Authority("REGISTRATION"),
            Authority("FINANCIAL"),
            Authority("DASHBOARDS")
        )
    }
}
