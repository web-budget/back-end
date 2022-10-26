package br.com.webbudget.mappers.administration

import br.com.webbudget.application.mappers.configuration.UserMapperImpl
import br.com.webbudget.application.mappers.configuration.UserMapper
import br.com.webbudget.application.payloads.configuration.UserForm
import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class UserMapperTest {

    private val userMapper: UserMapper = UserMapperImpl()

    @Test
    fun `should map user form to user`() {

        val authorities = listOf("SOMETHING")
        val userForm = UserForm(false, "Someone", "someone@test.com", "s3cr3t", authorities)

        val user = userMapper.map(userForm)

        assertThat(user)
            .isNotNull
            .hasFieldOrPropertyWithValue("name", userForm.name)
            .hasFieldOrPropertyWithValue("email", userForm.email)
            .hasFieldOrPropertyWithValue("active", userForm.active)
            .hasFieldOrPropertyWithValue("password", userForm.password)
    }

    @Test
    fun `should map user to user view`() {

        val externalId = UUID.randomUUID()
        val user = User("Someone", "someone@test.com", "s3cr3t", true, listOf())
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        val grants = listOf(Grant(user, Authority("SOMETHING")))
        user.apply { this.grants = grants }

        val userView = userMapper.map(user)

        assertThat(userView)
            .isNotNull
            .hasFieldOrPropertyWithValue("id", externalId)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("active", user.active)
            .hasFieldOrPropertyWithValue("authorities", listOf("SOMETHING"))
    }
}