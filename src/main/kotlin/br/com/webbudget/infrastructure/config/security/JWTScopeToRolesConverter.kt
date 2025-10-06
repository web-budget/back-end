package br.com.webbudget.infrastructure.config.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class JWTScopeToRolesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> = jwt.getClaimAsStringList("scope")
        .map { it.replace("ROLE_", "") }
        .map { SimpleGrantedAuthority("ROLE_$it") }
        .toList()
}