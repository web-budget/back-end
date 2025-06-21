package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.mappers.registration.FinancialPeriodMapper
import br.com.webbudget.application.payloads.financial.PeriodMovementCreateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementListView
import br.com.webbudget.application.payloads.financial.PeriodMovementUpdateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementView
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.Named
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(config = MappingConfiguration::class, uses = [ApportionmentMapper::class, FinancialPeriodMapper::class])
abstract class PeriodMovementMapper {

    @Autowired
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToView(periodMovement: PeriodMovement): PeriodMovementView

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToListView(periodMovement: PeriodMovement): PeriodMovementListView

    @Mappings(
        Mapping(target = "state", constant = "OPEN"),
        Mapping(target = "financialPeriod", source = "financialPeriod", qualifiedByName = ["mapFinancialPeriod"])
    )
    abstract fun mapToDomain(form: PeriodMovementCreateForm): PeriodMovement

    @Mapping(target = "financialPeriod", source = "financialPeriod", qualifiedByName = ["mapFinancialPeriod"])
    abstract fun mapToDomain(form: PeriodMovementUpdateForm, @MappingTarget periodMovement: PeriodMovement)

    @AfterMapping
    @Suppress("UnusedPrivateMember")
    fun afterMapFormToDomain(form: PeriodMovementCreateForm, @MappingTarget periodMovement: PeriodMovement) {
        periodMovement.apportionments.forEach { it.periodMovement = periodMovement }
    }

    @Named("mapFinancialPeriod")
    fun mapFinancialPeriod(id: UUID): FinancialPeriod = financialPeriodRepository.findByExternalId(id)
        ?: throw ResourceNotFoundException()
}