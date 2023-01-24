package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.domain.services.administration.UserAccountValidationService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.jdbc.Sql
import java.util.UUID

class UserAccountServiceTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var userAccountValidationService: UserAccountValidationService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userAccountService: UserAccountService

    @Test
    @Sql("/sql/clear-database.sql", "/sql/create-authorities.sql")
    fun `should save when validation pass`() {

        val toCreate = User("User", "user@webbudget.com.br", false, "s3cr3t")

        every { userAccountValidationService.validateOnCreate(any()) } just runs

        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))
        val created = userRepository.findByExternalId(externalId)

        assertThat(created)
            .isNotNull
            .hasFieldOrProperty("id").isNotNull
            .hasFieldOrProperty("externalId").isNotNull
            .hasFieldOrProperty("createdOn").isNotNull
            .hasFieldOrProperty("version").isNotNull
            .hasFieldOrProperty("password").isNotNull
            .hasFieldOrPropertyWithValue("active", toCreate.active)
            .hasFieldOrPropertyWithValue("name", toCreate.name)
            .hasFieldOrPropertyWithValue("email", toCreate.email)
            .extracting {
                assertThat(it!!.grants)
                    .hasSize(1)
                    .extracting("authority.name")
                    .containsExactlyInAnyOrder("ANY_AUTHORITY")
            }
    }

    @Test
    fun `should not save when validation fail`() {

        val toCreate = User("User", "user@webbudget.com.br", false, "s3cr3t")

        every { userAccountValidationService.validateOnCreate(any()) } throws
                RuntimeException("Ops, something went wrong!")

        assertThatThrownBy { userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY")) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @Sql("/sql/clear-database.sql", "/sql/create-authorities.sql")
    fun `should update when validation pass`() {

        val toCreate = User("User", "user@webbudget.com.br", false, "s3cr3t")

        every { userAccountValidationService.validateOnCreate(any()) } just runs
        every { userAccountValidationService.validateOnUpdate(any()) } just runs

        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))
        val toUpdate = userRepository.findByExternalId(externalId)

        assertThat(toUpdate).isNotNull

        toUpdate!!.apply {
            this.name = "Other"
            this.email = "other@webbudget.com.br"
            this.active = true
            this.grants = null
        }

        val updated = userAccountService.updateAccount(toUpdate, listOf("ANY_OTHER_AUTHORITY"))

        assertThat(updated)
            .isNotNull
            .hasFieldOrProperty("password").isNotNull
            .hasFieldOrPropertyWithValue("id", toUpdate.id)
            .hasFieldOrPropertyWithValue("externalId", toUpdate.externalId)
            .hasFieldOrPropertyWithValue("createdOn", toUpdate.createdOn)
            .hasFieldOrPropertyWithValue("active", toUpdate.active)
            .hasFieldOrPropertyWithValue("name", toUpdate.name)
            .hasFieldOrPropertyWithValue("email", toUpdate.email)
            .extracting {
                assertThat(it.version)
                    .isGreaterThan(toUpdate.version)

                assertThat(it.grants)
                    .isNotNull
                    .hasSize(1)
                    .extracting("authority.name")
                    .containsExactlyInAnyOrder("ANY_OTHER_AUTHORITY")
            }
    }

    @Test
    fun `should not update when validation fail`() {

        val toUpdate = User("User", "user@webbudget.com.br", false, "s3cr3t")
            .apply {
                this.id = 1L
                this.externalId = UUID.randomUUID()
            }

        every { userAccountValidationService.validateOnUpdate(any()) } throws
                RuntimeException("Ops, something went wrong!")

        assertThatThrownBy { userAccountService.updateAccount(toUpdate, listOf("ANY_OTHER_AUTHORITY")) }
            .isInstanceOf(RuntimeException::class.java)
    }

    @Test
    @Sql("/sql/clear-database.sql", "/sql/create-authorities.sql")
    fun `should delete`() {

        every { userAccountValidationService.validateOnCreate(any()) } just runs

        val toCreate = User("User", "user@webbudget.com.br", false, "s3cr3t")
        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toDelete = userRepository.findByExternalId(externalId)
        userAccountService.deleteAccount(toDelete!!)

        val found = userRepository.findByExternalId(externalId)
        assertThat(found).isNull()
    }

    @Test
    @Sql("/sql/clear-database.sql", "/sql/create-authorities.sql")
    fun `should update password`() {

        every { userAccountValidationService.validateOnCreate(any()) } just runs

        val newPassword = "new-secret"
        val toCreate = User("User", "user@webbudget.com.br", false, "s3cr3t")
        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toUpdate = userRepository.findByExternalId(externalId)!!
        userAccountService.updatePassword(toUpdate, newPassword)

        val updated = userRepository.findByExternalId(externalId)!!
        assertThat(passwordEncoder.matches(newPassword, updated.password)).isTrue
    }

    @Test
    @Sql("/sql/clear-database.sql", "/sql/create-authorities.sql")
    fun `should grant for all authorities`() {

        every { userAccountValidationService.validateOnCreate(any()) } just runs

        val toCreate = User("User", "user@webbudget.com.br", false, "s3cr3t")
        val authorities = listOf("SOME_AUTHORITY", "ANY_AUTHORITY", "ANY_OTHER_AUTHORITY")

        val externalId = userAccountService.createAccount(toCreate, authorities)
        val saved = userRepository.findByExternalId(externalId)!!

        assertThat(saved).extracting {
            assertThat(it.grants)
                .extracting("authority.name")
                .containsExactlyInAnyOrderElementsOf(authorities)
        }
    }
}
