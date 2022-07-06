package br.com.webbudget.application.converters

import br.com.webbudget.application.payloads.UserForm
import br.com.webbudget.domain.entities.configuration.User
import org.mapstruct.Mapper
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserFormToUserConverter : Converter<UserForm, User> {

    override fun convert(toConvert: UserForm): User?
}
