package br.com.webbudget

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    fun createPostgresContainer(): PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(IMAGE_NAME)
        .withReuse(true)

    companion object {
        private const val IMAGE_NAME = "postgres:15-alpine"
    }
}
