package br.com.webbudget.infrastructure.config.spring

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(
    basePackages = ["br.com.webbudget.infrastructure.repository"],
    repositoryBaseClass = BaseJpaRepositoryImpl::class
)
class PersistenceConfiguration
