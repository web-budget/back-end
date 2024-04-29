package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CardCreateForm
import br.com.webbudget.application.payloads.registration.CardUpdateForm
import br.com.webbudget.application.payloads.registration.CardView
import br.com.webbudget.domain.entities.registration.Card
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface CardMapper {

    @Mapping(source = "externalId", target = "id")
    fun map(card: Card): CardView

    fun map(form: CardCreateForm): Card

    fun map(form: CardUpdateForm, @MappingTarget card: Card)
}
