package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserView
import br.com.webbudget.domain.entities.configuration.Grant
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserToUserViewConverter : Converter<User, UserView> {

    @Mappings(
        Mapping(source = "externalId", target = "id"),
        Mapping(source = "grants", target = "authorities")
    )
    override fun convert(user: User): UserView?

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
