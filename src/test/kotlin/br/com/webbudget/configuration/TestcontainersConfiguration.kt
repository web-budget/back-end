package br.com.webbudget.configuration

import ch.martinelli.oss.testcontainers.mailpit.MailpitContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer = PostgreSQLContainer(DockerImageName.parse(POSTGRES_IMAGE))

    @Bean
    @ServiceConnection
    fun mailpitContainer(): MailpitContainer = MailpitContainer()

    companion object {
        const val POSTGRES_IMAGE = "postgres:17-alpine"
    }
}