package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.WalletValidator
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class WalletService(
    private val walletRepository: WalletRepository,
    @OnCreateValidation
    private val creationValidators: List<WalletValidator>,
    @OnUpdateValidation
    private val updateValidators: List<WalletValidator>
) {

    @Transactional
    fun create(wallet: Wallet): UUID {

        creationValidators.forEach { it.validate(wallet) }

        val created = walletRepository.persist(wallet)
        return created.externalId!!
    }

    @Transactional
    fun update(wallet: Wallet): Wallet {

        updateValidators.forEach { it.validate(wallet) }

        return walletRepository.merge(wallet)
    }

    @Transactional
    fun delete(wallet: Wallet) {
        walletRepository.delete(wallet)
    }
}
