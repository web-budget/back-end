package br.com.webbudget.domain.services.configuration

import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.validators.configuration.UserValidator
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
    private val userAccountValidationService: UserAccountValidationService
) {

    @Transactional
    fun createAccount(user: User, authorities: List<String>): UUID {

        userAccountValidationService.validateOnCreate(user)

        val password = passwordEncoder.encode(user.password)
        user.password = password

        val saved = userRepository.save(user)

        authorities.forEach {
            authorityRepository.findByName(it)
                ?.let { authority -> grantRepository.save(Grant(saved, authority)) }
        }

        return saved.externalId!!
    }

    @Transactional
    fun updateAccount(user: User, authorities: List<String>): User {

        userAccountValidationService.validateOnUpdate(user)

        grantRepository.deleteByUserExternalId(user.externalId!!)

        val saved = userRepository.save(user)

        authorities.forEach {
            authorityRepository.findByName(it)
                ?.let { authority -> grantRepository.save(Grant(saved, authority)) }
        }

        return userRepository.findByExternalId(saved.externalId!!)!!
    }

    @Transactional
    fun updatePassword(user: User, password: String) {

        val newPassword = passwordEncoder.encode(password)
        user.password = newPassword

        userRepository.save(user)
    }

    @Transactional
    fun deleteAccount(user: User) {
        userRepository.delete(user)
    }
}
