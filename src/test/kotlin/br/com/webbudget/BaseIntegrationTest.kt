package br.com.webbudget

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@TestMethodOrder(value = OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest {

    companion object {

        @Container
        private val redisContainer = GenericContainer<Nothing>("redis:6-alpine")
            .apply {
                withExposedPorts(6379)
                withCreateContainerCmdModifier { cmd -> cmd.withName("wb-test-cache") }
            }

        @Container
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:13-alpine")
            .apply {
                withExposedPorts(5432)
                withUsername("sa_webbudget")
                withPassword("sa_webbudget")
                withDatabaseName("webbudget")
                withCreateContainerCmdModifier { cmd -> cmd.withName("wb-test-database") }
            }

        @JvmStatic
        @DynamicPropertySource
        fun dynamicPropertiesRegister(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.redis.host", redisContainer::getHost)
            registry.add("spring.redis.port", redisContainer::getFirstMappedPort)
        }
    }
}
