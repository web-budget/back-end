package br.com.webbudget.domain.services.registration

import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FinancialPeriodService(
    private val financialPeriodRepository: FinancialPeriodRepository
) {

    @Transactional
    fun create(financialPeriod: FinancialPeriod): UUID {
        val created = financialPeriodRepository.merge(financialPeriod)
        return created.externalId!!
    }

    @Transactional
    fun delete(financialPeriod: FinancialPeriod) {
        return financialPeriodRepository.delete(financialPeriod)
    }
}