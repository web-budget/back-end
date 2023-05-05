package br.com.webbudget

import br.com.webbudget.utilities.MemoryLogAppender
import ch.qos.logback.classic.LoggerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@EnableAsync
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest internal constructor() {

    lateinit var memoryAppender: MemoryLogAppender

    fun startMemoryLoggerAppender() {
        memoryAppender = MemoryLogAppender()
        memoryAppender.context = LoggerFactory.getILoggerFactory() as LoggerContext

        val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        root.addAppender(memoryAppender)

        memoryAppender.start()
    }

    fun stopMemoryLoggerAppender() {
        memoryAppender.stop()
    }

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
