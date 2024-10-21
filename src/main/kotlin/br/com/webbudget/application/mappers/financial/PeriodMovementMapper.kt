package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.financial.PeriodMovementCreateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementUpdateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementView
import br.com.webbudget.domain.entities.financial.PeriodMovement
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.mapstruct.Named
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(config = MappingConfiguration::class, uses = [ApportionmentMapper::class])
abstract class PeriodMovementMapper {

    @Autowired
    private lateinit var financialPeriodRepository: FinancialPeriodRepository

    @Mapping(target = "id", source = "externalId")
    abstract fun map(periodMovement: PeriodMovement): PeriodMovementView

    @Mappings(
        Mapping(target = "state", constant = "OPEN"),
        Mapping(target = "financialPeriod", source = "financialPeriod", qualifiedByName = ["mapFinancialPeriod"])
    )
    abstract fun map(form: PeriodMovementCreateForm): PeriodMovement

    @Mapping(target = "financialPeriod", source = "financialPeriod", qualifiedByName = ["mapFinancialPeriod"])
    abstract fun map(form: PeriodMovementUpdateForm, @MappingTarget periodMovement: PeriodMovement)

    @Named("mapFinancialPeriod")
    fun mapFinancialPeriod(id: UUID): FinancialPeriod = financialPeriodRepository.findByExternalId(id)
        ?: throw ResourceNotFoundException(mapOf("financialPeriodId" to id))
}