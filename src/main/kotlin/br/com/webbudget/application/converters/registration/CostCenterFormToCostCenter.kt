package br.com.webbudget.application.converters.registration

import br.com.webbudget.application.converters.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CostCenterForm
import br.com.webbudget.domain.entities.registration.CostCenter
import org.mapstruct.Mapper
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface CostCenterFormToCostCenter : Converter<CostCenterForm, CostCenter> {

    override fun convert(toConvert: CostCenterForm): CostCenter?
}
