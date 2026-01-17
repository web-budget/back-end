package br.com.webbudget.application.mappers.administration

import br.com.webbudget.application.payloads.administration.UserCreateForm
import br.com.webbudget.application.payloads.administration.UserUpdateForm
import br.com.webbudget.application.payloads.administration.UserView
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun mapToView(user: User) = UserView(
        id = user.externalId!!,
        active = user.active,
        name = user.name,
        email = user.email,
        defaultLanguage = user.defaultLanguage,
        authorities = grantsToAuthorities(user.grants)
    )

    fun mapToDomain(form: UserCreateForm): User = User(
        name = form.name!!,
        email = form.email!!,
        password = form.password!!,
        defaultLanguage = form.defaultLanguage!!,
        grants = mutableListOf()
    )

    fun mapToDomain(form: UserUpdateForm, user: User): User = user.apply {
        this.active = form.active
        this.name = form.name!!
        this.defaultLanguage = form.defaultLanguage!!
    }

    private fun grantsToAuthorities(grants: List<Grant>): List<String> {
        return grants
            .map { it.role }
            .map { it.name }
            .toCollection(arrayListOf())
    }
}
