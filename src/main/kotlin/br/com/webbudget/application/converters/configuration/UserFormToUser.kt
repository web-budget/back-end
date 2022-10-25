package br.com.webbudget.application.converters.configuration

import br.com.webbudget.application.converters.MappingConfiguration
import br.com.webbudget.application.payloads.configuration.UserForm
import br.com.webbudget.domain.entities.administration.User
import org.mapstruct.Mapper
import org.springframework.core.convert.converter.Converter

@Mapper(config = MappingConfiguration::class)
interface UserFormToUser : Converter<UserForm, User> {

    override fun convert(toConvert: UserForm): User?
}
