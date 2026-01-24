package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.payloads.financial.RecurringMovementCreateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementListView
import br.com.webbudget.application.payloads.financial.RecurringMovementUpdateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementView
import br.com.webbudget.domain.entities.financial.RecurringMovement
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RecurringMovementMapper(
    private val classificationMapper: ClassificationMapper,
    private val classificationRepository: ClassificationRepository
) {

    fun mapToView(recurringMovement: RecurringMovement): RecurringMovementView = RecurringMovementView(
        id = recurringMovement.externalId!!,
        name = recurringMovement.name,
        value = recurringMovement.value,
        startingAt = recurringMovement.startingAt,
        state = recurringMovement.state.name,
        autoLaunch = recurringMovement.autoLaunch,
        indeterminate = recurringMovement.indeterminate,
        totalQuotes = recurringMovement.totalQuotes,
        startingQuote = recurringMovement.startingQuote,
        currentQuote = recurringMovement.currentQuote,
        classification = classificationMapper.mapToListView(recurringMovement.classification),
        description = recurringMovement.description
    )

    fun mapToListView(recurringMovement: RecurringMovement): RecurringMovementListView = RecurringMovementListView(
        id = recurringMovement.externalId!!,
        name = recurringMovement.name,
        value = recurringMovement.value,
        state = recurringMovement.state.name,
        autoLaunch = recurringMovement.autoLaunch,
        indeterminate = recurringMovement.indeterminate,
        totalQuotes = recurringMovement.totalQuotes,
        currentQuote = recurringMovement.currentQuote
    )

    fun mapToDomain(form: RecurringMovementCreateForm): RecurringMovement = RecurringMovement(
        name = form.name!!,
        value = form.value!!,
        startingAt = form.startingAt!!,
        autoLaunch = form.autoLaunch!!,
        classification = mapClassification(form.classification!!),
        indeterminate = form.indeterminate!!,
        totalQuotes = form.totalQuotes,
        startingQuote = form.startingQuote,
        currentQuote = form.currentQuote,
        description = form.description
    )

    fun mapToDomain(form: RecurringMovementUpdateForm, recurringMovement: RecurringMovement) = recurringMovement.apply {
        this.name = form.name!!
        this.classification = mapClassification(form.classification!!)
        this.startingAt = form.startingAt!!
        this.autoLaunch = form.autoLaunch!!
        this.description = form.description
    }

    private fun mapClassification(externalId: UUID): Classification =
        classificationRepository.findByExternalId(externalId) ?: throw ResourceNotFoundException()
}