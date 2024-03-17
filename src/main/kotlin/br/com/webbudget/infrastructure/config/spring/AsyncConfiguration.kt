package br.com.webbudget.infrastructure.config.spring

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@Profile("!test")
@Configuration(proxyBeanMethods = false)
class AsyncConfiguration