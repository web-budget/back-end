package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CostCenterForm
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.entities.registration.CostCenter
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface CostCenterMapper {

    @Mapping(source = "externalId", target = "id")
    fun map(costCenter: CostCenter): CostCenterView

    @Mapping(source = "active", target = "active", defaultValue = "true")
    fun map(form: CostCenterForm): CostCenter
}
