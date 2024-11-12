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

        val domainObject = costCenterMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.active).isEqualTo(form.active)
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
            })
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createCostCenter()
        val form = CostCenterUpdateForm("Other", "Other", false)

        costCenterMapper.mapToDomain(form, domainObject)

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

        val view = costCenterMapper.mapToView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(externalId)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.name).isEqualTo(domainObject.name)
                assertThat(it.description).isEqualTo(domainObject.description)
            })
    }

    @Test
    fun `should map domain object to list view`() {

        val externalId = UUID.randomUUID()
        val domainObject = createCostCenter(externalId = externalId)

        val view = costCenterMapper.mapToListView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(externalId)
                assertThat(it.active).isEqualTo(domainObject.active)
                assertThat(it.name).isEqualTo(domainObject.name)
            })
    }
}
