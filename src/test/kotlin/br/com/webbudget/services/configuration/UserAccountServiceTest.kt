package br.com.webbudget.services.configuration

import br.com.webbudget.domain.entities.configuration.Authority
import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.services.configuration.UserAccountService
import br.com.webbudget.domain.services.configuration.UserAccountValidationService
import br.com.webbudget.infrastructure.repository.configuration.AuthorityRepository
import br.com.webbudget.infrastructure.repository.configuration.GrantRepository
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
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

        val toCreate = User("To create", "test@test.com", "secret", true, listOf())

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

        val toCreate = User("To create", "test@test.com", "secret", true, listOf())

        every { userAccountValidationService.validateOnCreate(any()) } throws RuntimeException()

        assertThatThrownBy { userAccountService.createAccount(toCreate, listOf("ROLE")) }
            .isInstanceOf(java.lang.RuntimeException::class.java)

        verify(exactly = 1) { userAccountValidationService.validateOnCreate(any()) }

        verify { userRepository.save(toCreate) wasNot called }
        verify { grantRepository.save(any()) wasNot called }
    }

    @Test
    fun `should update when validation pass`() {

        val externalId = UUID.randomUUID()

        val toUpdate = User("To update", "test@test.com", "secret", true, listOf())
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

        val toUpdate = User("To update", "test@test.com", "secret", true, listOf())
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        val authority = Authority("ROLE")
        val grant = Grant(toUpdate, authority)

        every { userAccountValidationService.validateOnUpdate(any()) } throws java.lang.RuntimeException()

        assertThatThrownBy { userAccountService.updateAccount(toUpdate, listOf("ROLE")) }
            .isInstanceOf(java.lang.RuntimeException::class.java)

        verify(exactly = 1) { userAccountValidationService.validateOnUpdate(any()) }

        verify(exactly = 0) { grantRepository.deleteByUserExternalId(externalId) }
        verify { userRepository.save(toUpdate) wasNot called }
        verify { grantRepository.save(grant) wasNot called }
    }

    @Test
    fun `should delete`() {

    }

    @Test
    fun `should update password`() {

    }
}