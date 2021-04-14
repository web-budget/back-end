package br.com.webbudget.backend.infrastructure.config

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

class SecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder?) {
        super.configure(auth)
    }

    override fun configure(http: HttpSecurity?) {
        super.configure(http)
    }
}