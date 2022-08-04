package br.com.webbudget.application.converters.configuration

import br.com.webbudget.application.converters.MappingConfiguration
import br.com.webbudget.application.payloads.configuration.UserView
import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserToUserView : Converter<User, UserView> {

    @Mappings(
        Mapping(source = "externalId", target = "id"),
        Mapping(source = "grants", target = "authorities")
    )
    override fun convert(toConvert: User): UserView?

    companion object {
        @JvmStatic
        fun grantsToAuthorities(grants: List<Grant>): List<String> {
            return grants
                .map { it.authority }
                .map { it.name }
                .toCollection(arrayListOf())
        }
    }
}
