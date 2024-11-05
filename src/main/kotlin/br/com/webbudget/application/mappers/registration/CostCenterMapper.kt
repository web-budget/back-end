package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CostCenterCreateForm
import br.com.webbudget.application.payloads.registration.CostCenterListView
import br.com.webbudget.application.payloads.registration.CostCenterUpdateForm
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.entities.registration.CostCenter
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface CostCenterMapper {

    @Mapping(target = "id", source = "externalId")
    fun mapToView(costCenter: CostCenter): CostCenterView

    @Mapping(target = "id", source = "externalId")
    fun mapToListView(costCenter: CostCenter): CostCenterListView

    fun mapToDomain(form: CostCenterCreateForm): CostCenter

    fun mapToDomain(form: CostCenterUpdateForm, @MappingTarget costCenter: CostCenter)
}
