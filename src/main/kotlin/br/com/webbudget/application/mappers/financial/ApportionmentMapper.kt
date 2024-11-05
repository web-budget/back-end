package br.com.webbudget.application.mappers.financial

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.mappers.registration.MovementClassMapper
import br.com.webbudget.application.payloads.financial.ApportionmentForm
import br.com.webbudget.application.payloads.financial.ApportionmentView
import br.com.webbudget.domain.entities.financial.Apportionment
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(config = MappingConfiguration::class, uses = [MovementClassMapper::class])
abstract class ApportionmentMapper {

    @Autowired
    private lateinit var movementClassRepository: MovementClassRepository

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToView(apportionment: Apportionment): ApportionmentView

    @Mapping(target = "movementClass", source = "movementClass", qualifiedByName = ["mapMovementClass"])
    abstract fun mapToDomain(form: ApportionmentForm): Apportionment

    @Named("mapMovementClass")
    fun mapMovementClass(id: UUID): MovementClass = movementClassRepository.findByExternalId(id)
        ?: throw ResourceNotFoundException(mapOf("movementClassId" to id))
}