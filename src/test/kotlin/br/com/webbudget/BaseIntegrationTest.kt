package br.com.webbudget

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest internal constructor() {

    companion object {

        const val OBJECT_NOT_FOUND_ERROR = "Invalid state, object not found"

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
        }
    }
}
