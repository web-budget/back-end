package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class WalletService(
    private val walletRepository: WalletRepository,
    private val walletValidationService: WalletValidationService
) {

    @Transactional
    fun create(wallet: Wallet): UUID {
        walletValidationService.validateOnCreate(wallet)
        val created = walletRepository.persist(wallet)
        return created.externalId!!
    }

    @Transactional
    fun update(wallet: Wallet): Wallet {
        walletValidationService.validateOnUpdate(wallet)
        return walletRepository.merge(wallet)
    }

    @Transactional
    fun delete(wallet: Wallet) {
        walletRepository.delete(wallet)
    }
}
