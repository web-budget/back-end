package br.com.webbudget.domain.services

import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.validators.user.UserCreationValidator
import br.com.webbudget.infrastructure.repository.configuration.AuthorityRepository
import br.com.webbudget.infrastructure.repository.configuration.GrantRepository
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserAccountService(
    private val userRepository: UserRepository,
    private val grantRepository: GrantRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authorityRepository: AuthorityRepository,
    private val userCreationValidators: List<UserCreationValidator>
) {

    @Transactional
    fun createAccount(user: User, roles: List<String>): UUID {

        userCreationValidators.forEach { it.validate(user) }

        val password = passwordEncoder.encode(user.password)
        user.password = password

        val saved = userRepository.save(user)

        roles.forEach {
            authorityRepository.findByName(it)
                ?.let { authority -> grantRepository.save(Grant(saved, authority)) }
        }

        return saved.externalId!!
    }

    @Transactional
    fun updateAccount(externalId: UUID, user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun deleteAccount(externalId: UUID) {
        userRepository.deleteByExternalId(externalId)
    }
}
