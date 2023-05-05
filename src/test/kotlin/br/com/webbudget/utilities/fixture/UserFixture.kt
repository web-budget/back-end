package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.Language
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.domain.entities.administration.User
import java.util.UUID

object UserFixture {

    fun create() = create(password = "s3cr3t")

    fun create(id: Long, externalId: UUID, vararg authorities: String) = create("s3cr3t", *authorities)
        .apply {
            this.id = id
            this.externalId = externalId
        }

    fun create(password: String, vararg authorities: String): User {

        val user = create(password = password)

        val grants = authorities
            .map { Grant(user, Authority(it)) }
            .toCollection(mutableListOf())

        return user.apply { this.grants = grants }
    }

    fun create(
        active: Boolean = false,
        name: String = "User",
        email: String = "user@test.com",
        password: String = "s3cr3t",
        language: Language = PT_BR,
        grants: List<Grant> = listOf()
    ) = User(active, name, email, password, language, grants)
}
