package br.com.webbudget.utilities

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName.parse

class TestContainersInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(context: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            mapOf(
                "spring.datasource.url" to postgresContainer.jdbcUrl,
                "spring.datasource.username" to postgresContainer.username,
                "spring.datasource.password" to postgresContainer.password
            )
        ).applyTo(context.environment)
    }

    companion object {

        private val postgresContainer = PostgreSQLContainer<Nothing>(parse("postgres:15-alpine"))

        init {
            Startables.deepStart(postgresContainer).join()
        }
    }
}