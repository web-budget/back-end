package br.com.webbudget.services.administration

import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.domain.services.administration.UserAccountValidationService
import br.com.webbudget.infrastructure.repository.administration.AuthorityRepository
import br.com.webbudget.infrastructure.repository.administration.GrantRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.UserFixture
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@ExtendWith(MockKExtension::class)
class UserAccountServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var grantRepository: GrantRepository

    @MockK
    private lateinit var authorityRepository: AuthorityRepository

    @MockK
    private lateinit var userAccountValidationService: UserAccountValidationService

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMockKs
    private lateinit var userAccountService: UserAccountService

    @Test
    fun `should save when validation pass`() {

        val toCreate = UserFixture.create("secret")

        val authority = Authority("ROLE")
        val grant = Grant(toCreate, authority)

        every { userAccountValidationService.validateOnCreate(any()) } just runs
        every { passwordEncoder.encode("secret") } returns "s3cr3t"
        every { userRepository.save(any()) } returns toCreate.apply { this.externalId = UUID.randomUUID() }
        every { authorityRepository.findByName("ROLE") } returns authority
        every { grantRepository.save(grant) } returns grant

        userAccountService.createAccount(toCreate, listOf("ROLE"))

        verify(exactly = 1) { userAccountValidationService.validateOnCreate(any()) }
        verify(exactly = 1) { passwordEncoder.encode("secret") }
        verify(exactly = 1) { userRepository.save(toCreate) }
        verify(exactly = 1) { authorityRepository.findByName("ROLE") }
        verify(exactly = 1) { grantRepository.save(grant) }
    }

    @Test
    fun `should not save when validation fail`() {

        val toCreate = UserFixture.create("secret")

        every { userAccountValidationService.validateOnCreate(any()) } throws RuntimeException()

        assertThatThrownBy { userAccountService.createAccount(toCreate, listOf("ROLE")) }
            .isInstanceOf(RuntimeException::class.java)

        verify(exactly = 1) { userAccountValidationService.validateOnCreate(any()) }

        verify(exactly = 0) { passwordEncoder.encode("secret") }
        verify(exactly = 0) { userRepository.save(toCreate) }
        verify(exactly = 0) { authorityRepository.findByName("ROLE") }
        verify(exactly = 0) { grantRepository.save(any()) }
    }

    @Test
    fun `should update when validation pass`() {

        val externalId = UUID.randomUUID()

        val toUpdate = UserFixture.create("secret")
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        val authority = Authority("ROLE")
        val grant = Grant(toUpdate, authority)

        every { userAccountValidationService.validateOnUpdate(any()) } just runs
        every { grantRepository.deleteByUserExternalId(externalId) } just runs
        every { userRepository.save(any()) } returns toUpdate
        every { authorityRepository.findByName("ROLE") } returns authority
        every { grantRepository.save(grant) } returns grant
        every { userRepository.findByExternalId(externalId) } returns toUpdate

        userAccountService.updateAccount(toUpdate, listOf("ROLE"))

        verify(exactly = 1) { userAccountValidationService.validateOnUpdate(any()) }
        verify(exactly = 1) { grantRepository.deleteByUserExternalId(externalId) }
        verify(exactly = 1) { userRepository.save(toUpdate) }
        verify(exactly = 1) { authorityRepository.findByName("ROLE") }
        verify(exactly = 1) { grantRepository.save(grant) }
        verify(exactly = 1) { userRepository.findByExternalId(externalId) }
    }

    @Test
    fun `should not update when validation fail`() {

        val externalId = UUID.randomUUID()

        val toUpdate = UserFixture.create("secret")
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        val authority = Authority("ROLE")
        val grant = Grant(toUpdate, authority)

        every { userAccountValidationService.validateOnUpdate(any()) } throws RuntimeException()

        assertThatThrownBy { userAccountService.updateAccount(toUpdate, listOf("ROLE")) }
            .isInstanceOf(RuntimeException::class.java)

        verify(exactly = 1) { userAccountValidationService.validateOnUpdate(any()) }

        verify(exactly = 0) { grantRepository.deleteByUserExternalId(externalId) }
        verify(exactly = 0) { userRepository.save(toUpdate) }
        verify(exactly = 0) { grantRepository.save(grant) }
    }

    @Test
    fun `should delete`() {

        val toDelete = UserFixture.create("secret")

        every { userRepository.delete(toDelete) } just runs

        userAccountService.deleteAccount(toDelete)

        verify(exactly = 1) { userRepository.delete(toDelete) }
    }

    @Test
    fun `should update password`() {

        val newPassword = "new-secret"
        val toUpdate = UserFixture.create("secret")

        every { passwordEncoder.encode(newPassword) } returns "n3w-s3cr3t"
        every { userRepository.save(toUpdate) } returns toUpdate

        userAccountService.updatePassword(toUpdate, newPassword)

        verify(exactly = 1) { passwordEncoder.encode(newPassword) }
        verify(exactly = 1) {
            userRepository.save(withArg { user ->
                assertThat(user.password).isEqualTo("n3w-s3cr3t")
            })
        }
    }

    @Test
    fun `should grant for all authorities`() {

        val toCreate = UserFixture.create("secret")

        val authority = Authority("ROLE")
        val grant = Grant(toCreate, authority)

        every { userAccountValidationService.validateOnCreate(any()) } just runs
        every { passwordEncoder.encode("secret") } returns "s3cr3t"
        every { userRepository.save(any()) } returns toCreate.apply { this.externalId = UUID.randomUUID() }
        every { authorityRepository.findByName(any()) } returns authority
        every { grantRepository.save(any()) } returns grant

        userAccountService.createAccount(toCreate, listOf("ROLE1", "ROLE2", "ROLE3"))

        verify(exactly = 1) { userAccountValidationService.validateOnCreate(any()) }
        verify(exactly = 1) { passwordEncoder.encode("secret") }
        verify(exactly = 1) { userRepository.save(toCreate) }
        verify(exactly = 3) { authorityRepository.findByName(any()) }
        verify(exactly = 3) { grantRepository.save(grant) }
    }
}
