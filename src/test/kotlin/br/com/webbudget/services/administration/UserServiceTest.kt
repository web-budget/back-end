package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.application.payloads.administration.UserUpdateForm
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.administration.AccountActivationService
import br.com.webbudget.domain.services.administration.UserService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.createUser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD

@Sql(
    executionPhase = BEFORE_TEST_METHOD,
    scripts = [
        "/sql/administration/clear-tables.sql",
        "/sql/administration/create-authorities.sql"
    ]
)
@RecordApplicationEvents
class UserServiceTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var accountActivationService: AccountActivationService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `should save and not send account activation request`() {

        val userEmail = "user@webbudget.com.br"
        val toCreate = User(false, "User", userEmail, "s3cr3t", PT_BR)

        val externalId = userService.createAccount(toCreate, listOf("ANY_AUTHORITY"))
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

        verify(exactly = 0) { accountActivationService.requestActivation(userEmail) }

        confirmVerified(accountActivationService)
    }

    @Test
    fun `should save and send account activation request`() {

        val userEmail = "user@webbudget.com.br"
        val toCreate = User(false, "User", userEmail, "s3cr3t", PT_BR)

        every { accountActivationService.requestActivation(any<String>()) } just runs

        val externalId = userService.createAccount(toCreate, listOf("ANY_AUTHORITY"), true)
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

        verify(exactly = 1) { accountActivationService.requestActivation(ofType<String>()) }

        confirmVerified(accountActivationService)
    }

    @Test
    fun `should not save when duplicated username`() {

        val authorities = listOf("ANY_AUTHORITY")

        val toCreate = createUser()
        userService.createAccount(toCreate, authorities)

        val duplicated = createUser()

        assertThatThrownBy { userService.createAccount(duplicated, authorities) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    fun `should update`() {

        val toCreate = createUser()
        val form = UserUpdateForm(true, "Other", PT_BR, listOf("ANY_OTHER_AUTHORITY"))

        val externalId = userService.createAccount(toCreate, listOf("ANY_AUTHORITY"))
        val toUpdate = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.updateFields(form)
        val updated = userService.updateAccount(toUpdate, form.authorities)

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

        val authorities = listOf("ANY_AUTHORITY")

        val toCreate = createUser()
        userService.createAccount(toCreate, authorities)

        val duplicated = createUser(email = "duplicated@test.com")
        val externalId = userService.createAccount(duplicated, authorities)

        val toUpdate = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        toUpdate.apply {
            this.email = "user@test.com"
        }

        assertThatThrownBy { userService.updateAccount(toUpdate, authorities) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
    }

    @Test
    fun `should delete`() {

        val toCreate = createUser()
        val externalId = userService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toDelete = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        userService.deleteAccount(toDelete)

        val found = userRepository.findByExternalId(externalId)
        assertThat(found).isNull()
    }

    @Test
    fun `should update password`() {

        val newPassword = "new-secret"
        val toCreate = createUser()
        val externalId = userService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toUpdate = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        userService.updatePassword(toUpdate, newPassword)

        val updated = userRepository.findByExternalId(externalId)
            ?: fail(OBJECT_NOT_FOUND_ERROR)

        assertThat(passwordEncoder.matches(newPassword, updated.password)).isTrue
    }

    @Test
    fun `should grant for all authorities`() {

        val toCreate = createUser()
        val authorities = listOf("SOME_AUTHORITY", "ANY_AUTHORITY", "ANY_OTHER_AUTHORITY")

        val externalId = userService.createAccount(toCreate, authorities)
        val saved = userRepository.findByExternalId(externalId)!!

        assertThat(saved).extracting {
            assertThat(it.grants)
                .extracting("authority.name")
                .containsExactlyInAnyOrderElementsOf(authorities)
        }
    }

    @Test
    fun `should fail when try to delete admin user`() {

        val toCreate = createUser(email = "admin@webbudget.com.br")
        val externalId = userService.createAccount(toCreate, listOf("ANY_AUTHORITY"))

        val toDelete = userRepository.findByExternalId(externalId)

        assertThatThrownBy { userService.deleteAccount(toDelete!!) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
