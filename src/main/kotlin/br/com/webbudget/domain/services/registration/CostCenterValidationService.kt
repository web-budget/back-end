package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.CreatingValidation
import br.com.webbudget.domain.validators.DeletingValidation
import br.com.webbudget.domain.validators.UpdatingValidation
import br.com.webbudget.domain.validators.registration.CostCenterValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CostCenterValidationService(
    @CreatingValidation
    private val creatingValidators: List<CostCenterValidator>,
    @UpdatingValidation
    private val updatingValidation: List<CostCenterValidator>
) : ValidationService<CostCenter> {

    override fun validateOnCreate(value: CostCenter) {
        creatingValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: CostCenter) {
        updatingValidation.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: CostCenter) {
        TODO("Not yet implemented")
    }
}