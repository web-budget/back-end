package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.services.ValidationService
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnDeleteValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.FinancialPeriodValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FinancialPeriodValidationService(
    @OnCreateValidation
    private val creationValidators: List<FinancialPeriodValidator>,
    @OnUpdateValidation
    private val updateValidators: List<FinancialPeriodValidator>,
    @OnDeleteValidation
    private val deleteValidators: List<FinancialPeriodValidator>
) : ValidationService<FinancialPeriod> {

    override fun validateOnCreate(value: FinancialPeriod) {
        creationValidators.forEach { it.validate(value) }
    }

    override fun validateOnUpdate(value: FinancialPeriod) {
        updateValidators.forEach { it.validate(value) }
    }

    override fun validateOnDelete(value: FinancialPeriod) {
        deleteValidators.forEach { it.validate(value) }
    }
}