package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.FinancialPeriodCreateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodUpdateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodView
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface FinancialPeriodMapper {

    @Mapping(source = "externalId", target = "id")
    fun map(financialPeriod: FinancialPeriod): FinancialPeriodView

    @Mapping(target = "status", expression = "java(FinancialPeriod.Status.ACTIVE)")
    fun map(form: FinancialPeriodCreateForm): FinancialPeriod

    fun map(form: FinancialPeriodUpdateForm, @MappingTarget financialPeriod: FinancialPeriod)
}