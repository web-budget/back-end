package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.User
import java.util.UUID

object UserFixture {

    fun create(): User {
        return create("s3cr3t")
    }

    fun create(id: Long, externalId: UUID, vararg authorities: String): User {
        return create("s3cr3t", *authorities)
            .apply {
                this.id = id
                this.externalId = externalId
            }
    }

    fun create(password: String, vararg authorities: String): User {

        val user = User(false, "User", "user@test.com", password)

        val grants = authorities
            .map { Grant(user, Authority(it)) }
            .toCollection(mutableListOf())

        user.apply { this.grants = grants }

        return user
    }
}
