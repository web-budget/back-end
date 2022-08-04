package br.com.webbudget.application.converters.registration

import br.com.webbudget.application.converters.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.entities.registration.CostCenter
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface CostCenterToCostCenterView : Converter<CostCenter, CostCenterView> {

    @Mappings(
        Mapping(source = "externalId", target = "id")
    )
    override fun convert(toConvert: CostCenter): CostCenterView?
}
