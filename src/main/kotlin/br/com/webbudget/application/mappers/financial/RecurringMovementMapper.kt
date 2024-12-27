package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.financial.RecurringMovementCreateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementListView
import br.com.webbudget.application.payloads.financial.RecurringMovementUpdateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementView
import br.com.webbudget.domain.entities.financial.RecurringMovement
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class, uses = [ApportionmentMapper::class])
abstract class RecurringMovementMapper {

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToView(recurringMovement: RecurringMovement): RecurringMovementView

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToListView(recurringMovement: RecurringMovement): RecurringMovementListView

    @Mapping(target = "state", constant = "ACTIVE")
    abstract fun mapToDomain(form: RecurringMovementCreateForm): RecurringMovement

    abstract fun mapToDomain(form: RecurringMovementUpdateForm, @MappingTarget recurringMovement: RecurringMovement)

    @AfterMapping
    @Suppress("UnusedPrivateMember")
    fun afterMapFormToDomain(form: RecurringMovementCreateForm, @MappingTarget recurringMovement: RecurringMovement) {
        recurringMovement.apportionments.forEach { it.recurringMovement = recurringMovement }
    }
}