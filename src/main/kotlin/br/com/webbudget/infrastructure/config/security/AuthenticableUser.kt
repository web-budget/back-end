package br.com.webbudget.infrastructure.config.security

import br.com.webbudget.domain.entities.administration.User
import com.google.common.collect.ImmutableList
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

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
                .map { grant -> grant.role }
                .map { role -> SimpleGrantedAuthority(role.name) }
                .let { AuthenticableUser(user.email, user.password, user.active, it) }
        }
    }