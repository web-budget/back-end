package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.CostCenterValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CostCenterValidationService(
    @OnCreateValidation
    private val creationValidators: List<CostCenterValidator>,
    @OnUpdateValidation
    private val updateValidators: List<CostCenterValidator>
) : ValidationService<CostCenter> {

    override fun validateOnCreate(value: CostCenter) {
        creationValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: CostCenter) {
        updateValidators.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: CostCenter) {
        TODO("Not yet implemented")
    }
}