package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.payloads.registration.CostCenterCreateForm
import br.com.webbudget.application.payloads.registration.CostCenterUpdateForm
import br.com.webbudget.utilities.fixture.createCostCenter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class CostCenterMapperUTest {

    private val costCenterMapper = CostCenterMapperImpl()

    @Test
    fun `should map create form to domain object`() {

        val form = CostCenterCreateForm("Cost Center", "Some cost center", true)

        val domainObject = costCenterMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .hasFieldOrPropertyWithValue("active", form.active)
            .hasFieldOrPropertyWithValue("name", form.name)
            .hasFieldOrPropertyWithValue("description", form.description)
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createCostCenter()
        val form = CostCenterUpdateForm("Other", "Other", false)

        costCenterMapper.map(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.active).isEqualTo(form.active)
            })
    }

    @Test
    fun `should map domain object to view`() {

        val externalId = UUID.randomUUID()
        val domainObject = createCostCenter(externalId = externalId)

        val view = costCenterMapper.map(domainObject)

        assertThat(view)
            .isNotNull
            .hasFieldOrPropertyWithValue("id", externalId)
            .hasFieldOrPropertyWithValue("active", domainObject.active)
            .hasFieldOrPropertyWithValue("name", domainObject.name)
            .hasFieldOrPropertyWithValue("description", domainObject.description)
    }
}
