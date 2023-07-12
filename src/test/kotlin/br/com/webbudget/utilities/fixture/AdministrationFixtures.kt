package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.Language
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.domain.entities.administration.User
import java.util.UUID

@Suppress("LongParameterList")
fun createUser(
    id: Long? = 1L,
    externalId: UUID? = UUID.randomUUID(),
    active: Boolean = false,
    name: String = "User",
    email: String = "user@test.com",
    password: String = "s3cr3t",
    language: Language = PT_BR,
    vararg authorities: String
) = User(active, name, email, password, language, listOf())
    .apply {
        this.id = id
        this.externalId = externalId
        this.grants = authorities
            .map { authority -> Grant(this, Authority(authority)) }
            .toCollection(mutableListOf())
    }

