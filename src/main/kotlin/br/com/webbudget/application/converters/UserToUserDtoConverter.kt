package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserDto
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserToUserDtoConverter : Converter<User, UserDto> {

    @Mappings(
        Mapping(target = "password", ignore = true),
        Mapping(source = "externalId", target = "id")
    )
    override fun convert(user: User): UserDto?
}
