package br.com.webbudget.domain.validators.registration

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import org.springframework.stereotype.Component

@Component
@OnUpdateValidation
@OnCreateValidation
class WalletNameValidator(
    private val walletRepository: WalletRepository
) : WalletValidator {

    override fun validate(value: Wallet) {
        if (value.isSaved()) {
            this.validateSaved(value)
        } else {
            this.validateNotSaved(value)
        }
    }

    private fun validateSaved(value: Wallet) {
        walletRepository.findByNameIgnoreCaseAndExternalIdNot(value.name, value.externalId!!)
            ?.let { throw DuplicatedPropertyException("wallet.errors.duplicated-name", "wallet.name") }
    }

    private fun validateNotSaved(value: Wallet) {
        walletRepository.findByNameIgnoreCase(value.name)
            ?.let { throw DuplicatedPropertyException("wallet.errors.duplicated-name", "wallet.name") }
    }
}
