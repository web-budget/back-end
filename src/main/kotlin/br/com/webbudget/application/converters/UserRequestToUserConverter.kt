package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserRequest
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserRequestToUserConverter : Converter<UserRequest, User> {

    override fun convert(userRequest: UserRequest): User?
}
