package br.com.webbudget.application.mappers.configuration

import br.com.webbudget.application.mappers.MappingConfiguration
import br.com.webbudget.application.payloads.administration.UserCreateForm
import br.com.webbudget.application.payloads.administration.UserUpdateForm
import br.com.webbudget.application.payloads.administration.UserView
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.Mappings
import org.springframework.stereotype.Component

@Component
@Mapper(config = MappingConfiguration::class)
interface UserMapper {

    @Mappings(
        Mapping(target = "id", source = "externalId"),
        Mapping(target = "authorities", source = "grants")
    )
    fun mapToView(user: User): UserView

    @Mapping(target = "grants", expression = "java(java.util.List.of())")
    fun mapToDomain(form: UserCreateForm): User

    fun mapToDomain(form: UserUpdateForm, @MappingTarget user: User)

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
