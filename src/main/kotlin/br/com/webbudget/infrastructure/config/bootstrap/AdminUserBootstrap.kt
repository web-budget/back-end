package br.com.webbudget.infrastructure.config.bootstrap

import br.com.webbudget.domain.entities.administration.Language
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.services.administration.UserService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class AdminUserBootstrap(
    private val userService: UserService,
    private val userRepository: UserRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {

        val adminEmail = "admin@webbudget.com.br"

        if (userRepository.existsByEmail(adminEmail)) {
            logger.debug { "Admin user already exists, skipping bootstrap" }
            return
        }

        logger.warn { "No admin user found, creating default admin account" }

        val initialPassword = generateInitialPassword()

        val admin = User(
            active = true,
            name = "Administrador",
            email = adminEmail,
            password = initialPassword,
            defaultLanguage = Language.PT_BR
        )

        userService.createAccount(admin, ALL_ROLES)

        logger.info { "Admin account created successfully" }
        logger.warn { "Initial password is $initialPassword change it as soon as possible" }
    }

    private fun generateInitialPassword(): String = "Admin@" + (MIN_RANGE..MAX_RANGE).random()

    companion object {
        private const val MIN_RANGE = 1000
        private const val MAX_RANGE = 9999

        private val ALL_ROLES = listOf("ADMINISTRATION", "REGISTRATION", "FINANCIAL", "DASHBOARDS", "INVESTMENTS")
    }
}
