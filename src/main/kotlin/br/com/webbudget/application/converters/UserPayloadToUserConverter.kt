package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserPayload
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.factory.Mappers
import org.springframework.core.convert.converter.Converter

class UserPayloadToUserConverter : Converter<UserPayload, User> {

    override fun convert(userPayload: UserPayload): User {
        val mapper = Mappers.getMapper(UserMapper::class.java)
        return mapper.toModel(userPayload)
    }
}
