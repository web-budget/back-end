package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserPayload
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface UserMapper {

    @Mapping(source = "externalId", target = "id")
    fun toPayload(user: User): UserPayload

    fun toModel(userPayload: UserPayload): User
}
