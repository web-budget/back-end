package br.com.webbudget.domain.services.administration

import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.administration.UserValidator
import br.com.webbudget.infrastructure.repository.administration.AccountActivationAttemptRepository
import br.com.webbudget.infrastructure.repository.administration.RoleRepository
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
    private val roleRepository: RoleRepository,
    private val accountActivationService: AccountActivationService,
    private val passwordRecoverAttemptRepository: PasswordRecoverAttemptRepository,
    private val accountActivationAttemptRepository: AccountActivationAttemptRepository,
    @OnCreateValidation
    private val creationValidators: List<UserValidator>,
    @OnUpdateValidation
    private val updateValidators: List<UserValidator>
) {

    @Transactional
    fun createAccount(user: User, roles: List<String>, notifyAccountCreated: Boolean = false): UUID {

        creationValidators.forEach { it.validate(user) }

        val password = passwordEncoder.encode(user.password)
        user.password = password!!

        val saved = userRepository.persist(user)

        roles.forEach {
            roleRepository.findByName(it)?.let { authority -> grantRepository.persist(Grant(saved, authority)) }
        }

        if (notifyAccountCreated) {
            accountActivationService.requestActivation(saved.email)
        }

        return saved.externalId!!
    }

    @Transactional
    fun updateAccount(user: User, roles: List<String>): User {

        updateValidators.forEach { it.validate(user) }

        val userExternalId = user.externalId!!

        grantRepository.deleteByUserExternalId(userExternalId)

        val saved = userRepository.merge(user)

        roles.forEach {
            roleRepository.findByName(it)
                ?.let { role -> grantRepository.persist(Grant(saved, role)) }
        }

        val userGrants = grantRepository.findByUserExternalId(userExternalId)

        return saved.apply { this.grants = userGrants.toMutableList() }
    }

    @Transactional
    fun updatePassword(user: User, password: String, temporary: Boolean = true) {

        // TODO make the temporary thing work latter
        println(temporary)

        val newPassword = passwordEncoder.encode(password)
        user.password = newPassword!!

        userRepository.merge(user)
    }

    @Transactional
    fun deleteAccount(user: User) {

        require(!user.isMainAdmin()) { "user.errors.cannot-delete-admin" }

        val userExternalId = requireNotNull(user.externalId) { "user.errors.null-external-id" }

        passwordRecoverAttemptRepository.deleteByUserExternalId(userExternalId)
        accountActivationAttemptRepository.deleteByUserExternalId(userExternalId)

        userRepository.delete(user)
    }
}
