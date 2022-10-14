package br.com.webbudget.services.configuration

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.services.configuration.AuthenticationService
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockKExtension::class)
class AuthenticationServiceTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var authenticationService: AuthenticationService

    @Test
    fun `should find user and return as authenticable user`() {

        val expectedUser = User("The User", "user@user.com", "s3cr3t", true, listOf())

        every { userRepository.findByEmail(any()) } returns expectedUser

        val authenticable = authenticationService.loadUserByUsername("some@user.com")

        assertThat(authenticable)
            .isNotNull
            .hasFieldOrPropertyWithValue("username", expectedUser.email)
            .hasFieldOrPropertyWithValue("password", expectedUser.password)
            .hasFieldOrPropertyWithValue("enabled", expectedUser.active)
            .hasFieldOrPropertyWithValue("credentialsNonExpired", expectedUser.active)
            .hasFieldOrPropertyWithValue("accountNonExpired", expectedUser.active)
            .hasFieldOrPropertyWithValue("accountNonLocked", expectedUser.active)
    }

    @Test
    fun `should thrown error if no user is found`() {

        every { userRepository.findByEmail(any()) } returns null

        assertThatThrownBy { authenticationService.loadUserByUsername("some@user.com") }
            .isInstanceOf(UsernameNotFoundException::class.java)
    }
}
