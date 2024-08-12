package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FinancialPeriodService(
    private val financialPeriodRepository: FinancialPeriodRepository,
    private val financialPeriodValidationService: FinancialPeriodValidationService
) {

    @Transactional
    fun create(financialPeriod: FinancialPeriod): UUID {

        financialPeriodValidationService.validateOnCreate(financialPeriod)

        val created = financialPeriodRepository.merge(financialPeriod)
        return created.externalId!!
    }

    @Transactional
    fun update(financialPeriod: FinancialPeriod): FinancialPeriod {
        financialPeriodValidationService.validateOnUpdate(financialPeriod)
        return financialPeriodRepository.merge(financialPeriod)
    }

    @Transactional
    fun delete(financialPeriod: FinancialPeriod) {
        financialPeriodValidationService.validateOnDelete(financialPeriod)
        return financialPeriodRepository.delete(financialPeriod)
    }
}