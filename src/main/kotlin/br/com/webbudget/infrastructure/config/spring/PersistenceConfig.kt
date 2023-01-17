package br.com.webbudget.infrastructure.config.spring

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(
    basePackages = [
        "io.hypersistence.utils.spring.repository",
        "br.com.webbudget.infrastructure.repository"
    ]
)
class PersistenceConfig
