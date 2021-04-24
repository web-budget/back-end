package br.com.webbudget.backend.domain.services

import br.com.webbudget.backend.domain.entities.configuration.User
import br.com.webbudget.backend.infrastructure.repository.configuration.UserRepository
import com.google.common.collect.ImmutableList
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = this.userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Can't find user with given username $username")
        return AuthenticableUser.from(user)
    }

    data class AuthenticableUser(
        private val username: String,
        private val password: String,
        private val active: Boolean,
        private val authorities: List<GrantedAuthority>
    ) : UserDetails {

        override fun getAuthorities(): List<GrantedAuthority> {
            return ImmutableList.copyOf(this.authorities)
        }

        override fun getUsername(): String {
            return this.username
        }

        override fun getPassword(): String {
            return this.password
        }

        override fun isAccountNonExpired(): Boolean {
            return this.active
        }

        override fun isAccountNonLocked(): Boolean {
            return this.active
        }

        override fun isCredentialsNonExpired(): Boolean {
            return this.active
        }

        override fun isEnabled(): Boolean {
            return this.active
        }

        companion object {
            fun from(user: User): AuthenticableUser {

                val authorities = user.grants
                    .map { grant -> grant.authority }
                    .map { authority -> SimpleGrantedAuthority(authority.name) }

                return AuthenticableUser(user.email, user.password, user.active, authorities)
            }
        }
    }
}
