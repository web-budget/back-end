package br.com.webbudget.mappers.administration

import br.com.webbudget.application.mappers.configuration.UserMapper
import br.com.webbudget.application.mappers.configuration.UserMapperImpl
import br.com.webbudget.application.payloads.administration.UserCreateForm
import br.com.webbudget.application.payloads.administration.UserUpdateForm
import br.com.webbudget.domain.entities.administration.Authority
import br.com.webbudget.domain.entities.administration.Grant
import br.com.webbudget.domain.entities.administration.Language.EN_US
import br.com.webbudget.domain.entities.administration.Language.PT_BR
import br.com.webbudget.utilities.fixture.createUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class UserMapperUTest {

    private val userMapper: UserMapper = UserMapperImpl()

    @Test
    fun `should map create form to domain object`() {

        val authorities = listOf("SOMETHING")
        val form = UserCreateForm("Someone", "someone@test.com", "s3cr3t", PT_BR, authorities)

        val domainObject = userMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.active).isEqualTo(false)
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.email).isEqualTo(form.email)
                assertThat(it.password).isEqualTo(form.password)
                assertThat(it.defaultLanguage).isEqualTo(form.defaultLanguage)
            })
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createUser()
        val form = UserUpdateForm(false, "The Name", EN_US, listOf("SOMETHING"))

        userMapper.map(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.defaultLanguage).isEqualTo(form.defaultLanguage)
            })
    }

    @Test
    fun `should map domain object to view`() {

        val externalId = UUID.randomUUID()
        val domainObject = createUser(externalId = externalId)

        val grants = listOf(Grant(domainObject, Authority("SOMETHING")))

        domainObject.apply {
            this.grants = grants
        }

        val view = userMapper.map(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.email).isEqualTo(domainObject.email)
                assertThat(it.defaultLanguage).isEqualTo(domainObject.defaultLanguage)
                assertThat(it.authorities)
                    .isNotEmpty
                    .hasSize(1)
                    .containsExactlyElementsOf(listOf("SOMETHING"))
            })
    }
}
