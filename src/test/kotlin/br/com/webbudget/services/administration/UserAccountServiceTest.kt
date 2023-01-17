package br.com.webbudget.services.administration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.domain.services.administration.UserAccountValidationService
import br.com.webbudget.infrastructure.repository.administration.AuthorityRepository
import br.com.webbudget.infrastructure.repository.administration.GrantRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserAccountServiceTest : BaseIntegrationTest() {

    @MockkBean
    private lateinit var userAccountValidationService: UserAccountValidationService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var grantRepository: GrantRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var userAccountService: UserAccountService

    @BeforeEach
    fun clearDatabase() {
        grantRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
        authorityRepository.deleteAllInBatch()
        authorityRepository.persist(Authority("ANY_AUTHORITY"))
        authorityRepository.persist(Authority("ANY_OTHER_AUTHORITY"))
    }

    @Test
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
                    .hasSize(1)
                    .extracting("authority.name")
                    .containsExactlyInAnyOrder("ANY_OTHER_AUTHORITY")
            }
    }

//    @Test
//    fun `should not update when validation fail`() {
//
//        val externalId = UUID.randomUUID()
//
//        val toUpdate = UserFixture.create("secret")
//            .apply {
//                this.id = 1L
//                this.externalId = externalId
//            }
//
//        val authority = Authority("ROLE")
//        val grant = Grant(toUpdate, authority)
//
//        every { userAccountValidationService.validateOnUpdate(any()) } throws RuntimeException()
//
//        assertThatThrownBy { userAccountService.updateAccount(toUpdate, listOf("ROLE")) }
//            .isInstanceOf(RuntimeException::class.java)
//
//        verify(exactly = 1) { userAccountValidationService.validateOnUpdate(any()) }
//
//        verify(exactly = 0) { grantRepository.deleteByUserExternalId(externalId) }
//        verify(exactly = 0) { userRepository.save(toUpdate) }
//        verify(exactly = 0) { grantRepository.save(grant) }
//    }
//
//    @Test
//    fun `should delete`() {
//
//        val toDelete = UserFixture.create("secret")
//
//        every { userRepository.delete(toDelete) } just runs
//
//        userAccountService.deleteAccount(toDelete)
//
//        verify(exactly = 1) { userRepository.delete(toDelete) }
//    }
//
//    @Test
//    fun `should update password`() {
//
//        val newPassword = "new-secret"
//        val toUpdate = UserFixture.create("secret")
//
//        every { passwordEncoder.encode(newPassword) } returns "n3w-s3cr3t"
//        every { userRepository.save(toUpdate) } returns toUpdate
//
//        userAccountService.updatePassword(toUpdate, newPassword)
//
//        verify(exactly = 1) { passwordEncoder.encode(newPassword) }
//        verify(exactly = 1) {
//            userRepository.save(withArg { user ->
//                assertThat(user.password).isEqualTo("n3w-s3cr3t")
//            })
//        }
//    }
//
//    @Test
//    fun `should grant for all authorities`() {
//
//        val toCreate = UserFixture.create("secret")
//
//        val authority = Authority("ROLE")
//        val grant = Grant(toCreate, authority)
//
//        every { userAccountValidationService.validateOnCreate(any()) } just runs
//        every { passwordEncoder.encode("secret") } returns "s3cr3t"
//        every { userRepository.save(any()) } returns toCreate.apply { this.externalId = UUID.randomUUID() }
//        every { authorityRepository.findByName(any()) } returns authority
//        every { grantRepository.save(any()) } returns grant
//
//        userAccountService.createAccount(toCreate, listOf("ROLE1", "ROLE2", "ROLE3"))
//
//        verify(exactly = 1) { userAccountValidationService.validateOnCreate(any()) }
//        verify(exactly = 1) { passwordEncoder.encode("secret") }
//        verify(exactly = 1) { userRepository.save(toCreate) }
//        verify(exactly = 3) { authorityRepository.findByName(any()) }
//        verify(exactly = 3) { grantRepository.save(grant) }
//    }
}
