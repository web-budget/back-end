package br.com.webbudget.bootstrap

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.administration.Language
import br.com.webbudget.infrastructure.config.bootstrap.AdminUserBootstrap
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.memoryLogAppender
import br.com.webbudget.utilities.startMemoryLogAppender
import br.com.webbudget.utilities.stopMemoryLogAppender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.DefaultApplicationArguments
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

class AdminUserBootstrapUTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var adminUserBootstrap: AdminUserBootstrap

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should create admin when not present`() {

        val adminUser = userRepository.findByEmail("admin@webbudget.com.br")
            ?: fail { OBJECT_NOT_FOUND_ERROR }

        assertThat(adminUser).satisfies({
            assertThat(it.email).isEqualTo("admin@webbudget.com.br")
            assertThat(it.name).isEqualTo("Administrador")
            assertThat(it.active).isTrue()
            assertThat(it.password).isNotBlank()
            assertThat(it.defaultLanguage).isEqualTo(Language.PT_BR)
        })

        val expectedRoles = listOf("ADMINISTRATION", "REGISTRATION", "FINANCIAL", "DASHBOARDS", "INVESTMENTS")

        assertThat(adminUser.grants)
            .hasSize(5)
            .extracting("role.name")
            .containsExactlyInAnyOrderElementsOf(expectedRoles)
    }

    @Test
    fun `should skip creation when admin already exists`() {

        startMemoryLogAppender()

        adminUserBootstrap.run(DefaultApplicationArguments())

        stopMemoryLogAppender()

        assertThat(memoryLogAppender.countBy("Admin user already exists, skipping bootstrap")).isOne

        val users = userRepository.findAll(Specification.allOf(), PageRequest.ofSize(10))
            .filter { it.email == "admin@webbudget.com.br" }

        assertThat(users)
            .isNotEmpty
            .hasSize(1)
    }
}