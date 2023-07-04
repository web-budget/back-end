package br.com.webbudget.domain.services.administration

import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.infrastructure.repository.administration.AccountActivationAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.AuthorityRepository
import br.com.webbudget.infrastructure.repository.administration.GrantRepository
import br.com.webbudget.infrastructure.repository.administration.PasswordRecoverAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val grantRepository: GrantRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authorityRepository: AuthorityRepository,
    private val userValidationService: UserValidationService,
    private val accountActivationService: AccountActivationService,
    private val passwordRecoverAttemptRepository: PasswordRecoverAttemptRepository,
    private val accountActivationAttemptRepository: AccountActivationAttemptRepository
) {

    @Transactional
    fun createAccount(user: User, authorities: List<String>, notifyAccountCreated: Boolean = false): UUID {

        userValidationService.validateOnCreate(user)

        val password = passwordEncoder.encode(user.password)
        user.password = password

        val saved = userRepository.persist(user)

        authorities.forEach {
            authorityRepository.findByName(it)?.let { authority -> grantRepository.persist(Grant(saved, authority)) }
        }

        if (notifyAccountCreated) {
            accountActivationService.requestActivation(saved.email)
        }

        return saved.externalId!!
    }

    @Transactional
    fun updateAccount(user: User, authorities: List<String>): User {

        userValidationService.validateOnUpdate(user)

        val userExternalId = user.externalId!!

        grantRepository.deleteByUserExternalId(userExternalId)

        val saved = userRepository.merge(user)

        authorities.forEach {
            authorityRepository.findByName(it)?.let { authority -> grantRepository.persist(Grant(saved, authority)) }
        }

        val userGrants = grantRepository.findByUserExternalId(userExternalId)

        return saved.apply { this.grants = userGrants }
    }

    @Transactional
    fun updatePassword(user: User, password: String, temporary: Boolean = true) {

        // TODO make the temporary thing work latter
        println(temporary)

        val newPassword = passwordEncoder.encode(password)
        user.password = newPassword

        userRepository.update(user)
    }

    @Transactional
    fun deleteAccount(user: User) {

        require(!user.isAdmin()) { "user.errors.cannot-delete-admin" }

        val userExternalId = requireNotNull(user.externalId) { "user.errors.null-external-id" }

        passwordRecoverAttemptRepository.deleteByUserExternalId(userExternalId)
        accountActivationAttemptRepository.deleteByUserExternalId(userExternalId)

        userRepository.delete(user)
    }
}
