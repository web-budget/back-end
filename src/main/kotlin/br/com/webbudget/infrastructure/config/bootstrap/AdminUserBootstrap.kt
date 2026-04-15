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

        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            logger.debug { "Admin user already exists, skipping bootstrap" }
            return
        }

        logger.warn { "No admin user found, creating default admin account" }

        val initialPassword = generateInitialPassword()

        val admin = User(
            active = true,
            name = "Administrador",
            email = ADMIN_EMAIL,
            password = initialPassword,
            defaultLanguage = Language.PT_BR
        )

        userService.createAccount(admin, ALL_ROLES)

        logger.info { "Admin account created successfully" }
        logger.warn { "Initial password is $initialPassword change it as soon as possible" }
    }

    private fun generateInitialPassword(): String = "Admin@" + (1000..9999).random()

    companion object {
        private const val ADMIN_EMAIL = "admin@webbudget.com.br"
        private val ALL_ROLES = listOf("ADMINISTRATION", "REGISTRATION", "FINANCIAL", "DASHBOARDS", "INVESTMENTS")
    }
}
