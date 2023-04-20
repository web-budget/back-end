package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.CreatingValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.domain.validators.registration.WalletValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WalletValidationService(
    @CreatingValidation
    private val creatingValidators: List<WalletValidator>,
    @UpdatingValidation
    private val updatingValidation: List<WalletValidator>
) : ValidationService<Wallet> {

    override fun validateOnCreate(value: Wallet) {
        creatingValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: Wallet) {
        updatingValidation.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: Wallet) {
        TODO("Not yet implemented")
    }
}
