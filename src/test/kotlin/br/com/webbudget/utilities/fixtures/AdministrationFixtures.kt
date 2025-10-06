package br.com.webbudget.utilities.fixtures

import br.com.webbudget.domain.entities.administration.Role
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.Language
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.domain.entities.administration.User
import java.util.UUID

fun createUser(
    id: Long? = null,
    externalId: UUID? = UUID.randomUUID(),
    active: Boolean = false,
    name: String = "User",
    email: String = "user@test.com",
    password: String = "s3cr3t",
    language: Language = PT_BR,
    vararg authorities: String
) = User(active, name, email, password, language, mutableListOf())
    .apply {
        this.id = id
        this.externalId = externalId
        this.grants = authorities
            .map { authority -> Grant(this, Role(authority)) }
            .toCollection(mutableListOf())
    }

