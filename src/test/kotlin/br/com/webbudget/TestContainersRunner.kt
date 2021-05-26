package br.com.webbudget

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class TestContainersRunner {

    companion object {

        @Container
        private val redisContainer = KGenericContainer("redis:6-alpine")
            .withExposedPorts(6379)

        @Container
        private val postgresContainer = KPostgreSQLContainer("postgres:13-alpine")
            .withDatabaseName("webbudget")

        @JvmStatic
        @DynamicPropertySource
        fun dynamicPropertiesRegister(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)

            registry.add("spring.redis.host", redisContainer::getHost)
            registry.add("spring.redis.port", redisContainer::getFirstMappedPort)
        }
    }
}

internal class KGenericContainer(image: String) : GenericContainer<KGenericContainer>(image)

internal class KPostgreSQLContainer(image: String) : PostgreSQLContainer<KPostgreSQLContainer>(image)
