package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CardCreateForm
import br.com.webbudget.application.payloads.registration.CardListView
import br.com.webbudget.application.payloads.registration.CardUpdateForm
import br.com.webbudget.application.payloads.registration.CardView
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Mapper(config = MappingConfiguration::class, uses = [WalletMapper::class])
abstract class CardMapper {

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToView(card: Card): CardView

    @Mapping(target = "id", source = "externalId")
    abstract fun mapToListView(card: Card): CardListView

    @Mapping(target = "wallet", expression = "java(mapWallet(form.getWallet()))")
    abstract fun mapToDomain(form: CardCreateForm): Card

    @Mapping(target = "wallet", expression = "java(mapWallet(form.getWallet()))")
    abstract fun mapToDomain(form: CardUpdateForm, @MappingTarget card: Card)

    fun mapWallet(id: UUID): Wallet = walletRepository.findByExternalId(id)
        ?: throw ResourceNotFoundException(mapOf("walletId" to id.toString()))
}
