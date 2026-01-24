package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapper
import br.com.webbudget.application.payloads.financial.PeriodMovementCreateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementListView
import br.com.webbudget.application.payloads.financial.PeriodMovementUpdateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementView
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PeriodMovementMapper(
    private val classificationMapper: ClassificationMapper,
    private val financialPeriodMapper: FinancialPeriodMapper,
    private val classificationRepository: ClassificationRepository,
    private val financialPeriodRepository: FinancialPeriodRepository
) {

    fun mapToView(periodMovement: PeriodMovement): PeriodMovementView = PeriodMovementView(
        id = periodMovement.externalId!!,
        name = periodMovement.name,
        dueDate = periodMovement.dueDate,
        value = periodMovement.value,
        state = periodMovement.state.name,
        classification = classificationMapper.mapToListView(periodMovement.classification),
        financialPeriod = financialPeriodMapper.mapToListView(periodMovement.financialPeriod),
        quoteNumber = periodMovement.quoteNumber,
        description = periodMovement.description
    )

    fun mapToListView(periodMovement: PeriodMovement): PeriodMovementListView = PeriodMovementListView(
        id = periodMovement.externalId!!,
        name = periodMovement.name,
        dueDate = periodMovement.dueDate,
        value = periodMovement.value,
        state = periodMovement.state.name,
        financialPeriod = financialPeriodMapper.mapToListView(periodMovement.financialPeriod),
    )

    fun mapToDomain(form: PeriodMovementCreateForm): PeriodMovement = PeriodMovement(
        name = form.name!!,
        dueDate = form.dueDate!!,
        value = form.value!!,
        classification = mapClassification(form.classification!!),
        financialPeriod = mapFinancialPeriod(form.financialPeriod!!),
        description = form.description
    )

    fun mapToDomain(form: PeriodMovementUpdateForm, periodMovement: PeriodMovement) = periodMovement.apply {
        this.name = form.name!!
        this.dueDate = form.dueDate!!
        this.value = form.value!!
        this.financialPeriod = mapFinancialPeriod(form.financialPeriod!!)
        this.classification = mapClassification(form.classification!!)
        this.description = form.description
    }

    private fun mapFinancialPeriod(externalId: UUID): FinancialPeriod =
        financialPeriodRepository.findByExternalId(externalId) ?: throw ResourceNotFoundException()

    private fun mapClassification(externalId: UUID): Classification =
        classificationRepository.findByExternalId(externalId) ?: throw ResourceNotFoundException()
}