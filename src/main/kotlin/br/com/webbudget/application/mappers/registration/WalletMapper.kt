package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.WalletCreateForm
import br.com.webbudget.application.payloads.registration.WalletView
import br.com.webbudget.domain.entities.registration.Wallet
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface WalletMapper {

    @Mapping(source = "externalId", target = "id")
    fun map(wallet: Wallet): WalletView

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "currentBalance", expression = "java(java.math.BigDecimal.ZERO)")
    fun map(form: WalletCreateForm): Wallet
}
