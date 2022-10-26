package br.com.webbudget.infrastructure.config.spring

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@Configuration
@EnableScheduling
@EnableJpaAuditing
class CommonsConfiguration
