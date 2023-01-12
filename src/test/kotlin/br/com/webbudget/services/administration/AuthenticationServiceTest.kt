package br.com.webbudget.services.administration

import br.com.webbudget.domain.services.administration.AuthenticationService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import br.com.webbudget.utilities.fixture.UserFixture
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

        val expectedUser = UserFixture.create()

        every { userRepository.findByEmail(any()) } returns expectedUser

        val authenticable = authenticationService.loadUserByUsername("someone@test.com")

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

        assertThatThrownBy { authenticationService.loadUserByUsername("someone@test.com") }
            .isInstanceOf(UsernameNotFoundException::class.java)
    }
}
