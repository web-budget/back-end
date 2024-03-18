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
class BankingInformationValidator(
    private val walletRepository: WalletRepository
) : WalletValidator {

    override fun validate(value: Wallet) {
        if (value.hasValidBankInformation()) {
            if (value.isSaved()) {
                this.validateSaved(value)
            } else {
                this.validateNotSaved(value)
            }
        }
    }

    private fun validateSaved(value: Wallet) {
        walletRepository.findByBankInfo(value.bank, value.agency, value.number, value.externalId!!)
            ?.let { throw DuplicatedPropertyException("wallet.errors.duplicated-bank-info", "wallet.bank-info") }
    }

    private fun validateNotSaved(value: Wallet) {
        walletRepository.findByBankInfo(value.bank, value.agency, value.number)
            ?.let { throw DuplicatedPropertyException("wallet.errors.duplicated-bank-info", "wallet.bank-info") }
    }
}
