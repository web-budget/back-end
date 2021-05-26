package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserDto
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserDtoToUserConverter : Converter<UserDto, User> {

    override fun convert(userDto: UserDto): User?
}
