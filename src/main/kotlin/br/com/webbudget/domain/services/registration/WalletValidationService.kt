package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.WalletValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WalletValidationService(
    @OnCreateValidation
    private val creationValidators: List<WalletValidator>,
    @OnUpdateValidation
    private val updateValidators: List<WalletValidator>
) : ValidationService<Wallet> {

    override fun validateOnCreate(value: Wallet) {
        creationValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: Wallet) {
        updateValidators.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: Wallet) {
        TODO("Not yet implemented")
    }
}
