package br.com.webbudget.mappers.administration

import br.com.webbudget.application.mappers.configuration.UserMapper
import br.com.webbudget.application.mappers.configuration.UserMapperImpl
import br.com.webbudget.application.payloads.administration.UserCreateForm
import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.utilities.fixture.createUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class UserMapperTest {

    private val userMapper: UserMapper = UserMapperImpl()

    @Test
    fun `should map user form to user`() {

        val authorities = listOf("SOMETHING")
        val userCreateForm = UserCreateForm("Someone", "someone@test.com", "s3cr3t", PT_BR, authorities)

        val user = userMapper.map(userCreateForm)

        assertThat(user)
            .isNotNull
            .hasFieldOrPropertyWithValue("active", false)
            .hasFieldOrPropertyWithValue("name", userCreateForm.name)
            .hasFieldOrPropertyWithValue("email", userCreateForm.email)
            .hasFieldOrPropertyWithValue("password", userCreateForm.password)
            .hasFieldOrPropertyWithValue("defaultLanguage", userCreateForm.defaultLanguage)
    }

    @Test
    fun `should map user to user view`() {

        val externalId = UUID.randomUUID()
        val user = createUser(externalId = externalId)

        val grants = listOf(Grant(user, Authority("SOMETHING")))
        user.apply { this.grants = grants }

        val userView = userMapper.map(user)

        assertThat(userView)
            .isNotNull
            .hasFieldOrPropertyWithValue("id", externalId)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("active", user.active)
            .hasFieldOrPropertyWithValue("defaultLanguage", user.defaultLanguage)
            .hasFieldOrPropertyWithValue("authorities", listOf("SOMETHING"))
    }
}
