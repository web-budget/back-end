package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.MovementClassView
import br.com.webbudget.application.payloads.registration.MovementClassCreateForm
import br.com.webbudget.application.payloads.registration.MovementClassUpdateForm
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(config = MappingConfiguration::class, uses = [CostCenterMapper::class])
abstract class MovementClassMapper {

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Mapping(source = "externalId", target = "id")
    abstract fun map(movementClass: MovementClass): MovementClassView

    @Mapping(target = "costCenter", expression = "java(mapCostCenter(form.getCostCenter()))")
    abstract fun map(form: MovementClassCreateForm): MovementClass

    @Mappings(
        Mapping(target = "type", ignore = true),
        Mapping(target = "costCenter", expression = "java(mapCostCenter(form.getCostCenter()))")
    )
    abstract fun map(form: MovementClassUpdateForm, @MappingTarget movementClass: MovementClass)

    fun mapCostCenter(externalId: UUID): CostCenter = costCenterRepository.findByExternalId(externalId)
        ?: throw ResourceNotFoundException(mapOf("costCenterId" to externalId))
}