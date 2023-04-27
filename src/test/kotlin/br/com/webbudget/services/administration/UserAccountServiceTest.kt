package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.application.payloads.administration.UserUpdateForm
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.jdbc.Sql

class UserAccountServiceTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userAccountService: UserAccountService

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should save`() {

        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")

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
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should not save when duplicated username`() {

        val authorities = listOf("ANY_AUTHORITY")

        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")
        userAccountService.createAccount(toCreate, authorities)

        val duplicated = User(false, "User", "user@webbudget.com.br", "s3cr3t")

        assertThatThrownBy { userAccountService.createAccount(duplicated, authorities) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should update`() {

        val form = UserUpdateForm(true, "Other", listOf("ANY_OTHER_AUTHORITY"))
        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")

        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))
        val toUpdate = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.updateFields(form)
        val updated = userAccountService.updateAccount(toUpdate, form.authorities)

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
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should not update when validation fail`() {

        val authorities = listOf("ANY_AUTHORITY")

        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")
        userAccountService.createAccount(toCreate, authorities)

        val duplicated = User(false, "User", "duplicated@webbudget.com.br", "s3cr3t")
        val externalId = userAccountService.createAccount(duplicated, authorities)

        val toUpdate = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.email = "user@webbudget.com.br"
        }

        assertThatThrownBy { userAccountService.updateAccount(toUpdate, authorities) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should delete`() {

        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")
        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toDelete = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        userAccountService.deleteAccount(toDelete)

        val found = userRepository.findByExternalId(externalId)
        assertThat(found).isNull()
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should update password`() {

        val newPassword = "new-secret"
        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")
        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toUpdate = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        userAccountService.updatePassword(toUpdate, newPassword)

        val updated = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThat(passwordEncoder.matches(newPassword, updated.password)).isTrue
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should grant for all authorities`() {

        val toCreate = User(false, "User", "user@webbudget.com.br", "s3cr3t")
        val authorities = listOf("SOME_AUTHORITY", "ANY_AUTHORITY", "ANY_OTHER_AUTHORITY")

        val externalId = userAccountService.createAccount(toCreate, authorities)
        val saved = userRepository.findByExternalId(externalId)!!

        assertThat(saved).extracting {
            assertThat(it.grants)
                .extracting("authority.name")
                .containsExactlyInAnyOrderElementsOf(authorities)
        }
    }

    @Test
    @Sql("/sql/administration/clear-tables.sql", "/sql/administration/create-authorities.sql")
    fun `should fail when try to delete admin user`() {

        val toCreate = User(false, "Admin", "admin@webbudget.com.br", "s3cr3t")
        val externalId = userAccountService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toDelete = userRepository.findByExternalId(externalId)

        assertThatThrownBy { userAccountService.deleteAccount(toDelete!!) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
