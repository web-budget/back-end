package br.com.webbudget.domain.services

import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserAccountService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createAccount(user: User) {

        val password = passwordEncoder.encode(user.password)
        user.password = password

        userRepository.save(user)
    }

    @Transactional
    fun updateAccount(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun deleteAccount(externalId: UUID) {
        userRepository.deleteByExternalId(externalId)
    }
}
