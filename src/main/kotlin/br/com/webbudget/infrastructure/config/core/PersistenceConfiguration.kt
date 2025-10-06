package br.com.webbudget.infrastructure.config.core

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = ["br.com.webbudget.infrastructure.repository"],
    repositoryBaseClass = BaseJpaRepositoryImpl::class
)
class PersistenceConfiguration
