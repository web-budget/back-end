package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.CardCreateForm
import br.com.webbudget.application.payloads.registration.CardUpdateForm
import br.com.webbudget.application.payloads.registration.CardView
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Wallet
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

    @Mapping(source = "externalId", target = "id")
    abstract fun map(card: Card): CardView

    @Mapping(target = "wallet", expression = "java(mapWallet(form.getWallet()))")
    abstract fun map(form: CardCreateForm): Card

    @Mapping(target = "wallet", expression = "java(mapWallet(form.getWallet()))")
    abstract fun map(form: CardUpdateForm, @MappingTarget card: Card)

    fun mapWallet(externalId: UUID?): Wallet? = externalId?.let { walletRepository.findByExternalId(externalId) }
}
