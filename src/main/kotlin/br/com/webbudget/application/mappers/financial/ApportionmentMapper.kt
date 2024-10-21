package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.financial.ApportionmentForm
import br.com.webbudget.domain.entities.financial.Apportionment
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Named
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(config = MappingConfiguration::class)
abstract class ApportionmentMapper {

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Autowired
    private lateinit var movementClassRepository: MovementClassRepository

    @Mappings(
        Mapping(target = "costCenter", source = "costCenter", qualifiedByName = ["mapCostCenter"]),
        Mapping(target = "movementClass", source = "movementClass", qualifiedByName = ["mapMovementClass"]),
    )
    abstract fun map(form: ApportionmentForm): Apportionment

    @Named("mapCostCenter")
    fun mapCostCenter(id: UUID): CostCenter = costCenterRepository.findByExternalId(id)
        ?: throw ResourceNotFoundException(mapOf("costCenterId" to id))

    @Named("mapMovementClass")
    fun mapMovementClass(id: UUID): MovementClass = movementClassRepository.findByExternalId(id)
        ?: throw ResourceNotFoundException(mapOf("movementClassId" to id))
}