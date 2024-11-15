package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.validators.OnCreateValidation
import br.com.webbudget.domain.validators.OnDeleteValidation
import br.com.webbudget.domain.validators.OnUpdateValidation
import br.com.webbudget.domain.validators.registration.FinancialPeriodValidator
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FinancialPeriodService(
    private val financialPeriodRepository: FinancialPeriodRepository,
    @OnCreateValidation
    private val creationValidators: List<FinancialPeriodValidator>,
    @OnUpdateValidation
    private val updateValidators: List<FinancialPeriodValidator>,
    @OnDeleteValidation
    private val deleteValidators: List<FinancialPeriodValidator>
) {

    @Transactional
    fun create(financialPeriod: FinancialPeriod): UUID {

        creationValidators.forEach { it.validate(financialPeriod) }

        val created = financialPeriodRepository.merge(financialPeriod)
        return created.externalId!!
    }

    @Transactional
    fun update(financialPeriod: FinancialPeriod): FinancialPeriod {

        updateValidators.forEach { it.validate(financialPeriod) }

        return financialPeriodRepository.merge(financialPeriod)
    }

    @Transactional
    fun delete(financialPeriod: FinancialPeriod) {

        deleteValidators.forEach { it.validate(financialPeriod) }

        return financialPeriodRepository.delete(financialPeriod)
    }
}