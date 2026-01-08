package br.com.webbudget.application.mappers.registration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.registration.WalletCreateForm
import br.com.webbudget.application.payloads.registration.WalletListView
import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.application.payloads.registration.WalletView
import br.com.webbudget.domain.entities.registration.Wallet
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface WalletMapper {

    @Mapping(target = "id", source = "externalId")
    fun mapToView(wallet: Wallet): WalletView

    @Mapping(target = "id", source = "externalId")
    fun mapToListView(wallet: Wallet): WalletListView

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "currentBalance", expression = "java(java.math.BigDecimal.ZERO)")
    fun mapToDomain(form: WalletCreateForm): Wallet

    fun mapToDomain(form: WalletUpdateForm, @MappingTarget wallet: Wallet)
}
