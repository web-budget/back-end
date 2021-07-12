package br.com.webbudget.domain.services

import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.validators.user.UserCreationValidator
import br.com.webbudget.domain.validators.user.UserUpdatingValidator
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
    private val userCreationValidators: List<UserCreationValidator>,
    private val userUpdatingValidators: List<UserUpdatingValidator>
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
    fun updateAccount(user: User, roles: List<String>): User {

        userUpdatingValidators.forEach { it.validate(user) }

        grantRepository.deleteByUserExternalId(user.externalId!!)

        val saved = userRepository.save(user)

        roles.forEach {
            authorityRepository.findByName(it)
                ?.let { authority -> grantRepository.save(Grant(saved, authority)) }
        }

        return userRepository.findByExternalId(saved.externalId!!)!!
    }

    @Transactional
    fun deleteAccount(user: User) {
        userRepository.delete(user)
    }
}
