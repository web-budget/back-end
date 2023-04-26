package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.payloads.registration.CostCenterForm
import br.com.webbudget.domain.entities.registration.CostCenter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class CostCenterMapperTest {

    private val costCenterMapper: CostCenterMapper = CostCenterMapperImpl()

    @Test
    fun `should map form to domain object`() {

        val form = CostCenterForm("Cost Center", "Some cost center", true)

        val domainObject = costCenterMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .hasFieldOrPropertyWithValue("active", form.active)
            .hasFieldOrPropertyWithValue("name", form.name)
            .hasFieldOrPropertyWithValue("description", form.description)
    }

    @Test
    fun `should map domain object to view`() {

        val externalId = UUID.randomUUID()
        val domainObject = CostCenter("Cost Center", true, "Some cost center")
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        val view = costCenterMapper.map(domainObject)

        assertThat(view)
            .isNotNull
            .hasFieldOrPropertyWithValue("id", externalId)
            .hasFieldOrPropertyWithValue("active", domainObject.active)
            .hasFieldOrPropertyWithValue("name", domainObject.name)
            .hasFieldOrPropertyWithValue("description", domainObject.description)
    }
}
