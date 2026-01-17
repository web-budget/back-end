package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.payloads.registration.CostCenterCreateForm
import br.com.webbudget.application.payloads.registration.CostCenterUpdateForm
import br.com.webbudget.utilities.fixtures.createCostCenter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class CostCenterMapperUTest {

    private val costCenterMapper = CostCenterMapper()

    @Test
    fun `should map create form to domain object`() {

        val form = CostCenterCreateForm("Cost Center", "Some cost center", BigDecimal.ONE, BigDecimal.ONE)

        val domainObject = costCenterMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
            })
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createCostCenter()
        val form = CostCenterUpdateForm("Other", false, "Other", BigDecimal.ONE, BigDecimal.ONE)

        costCenterMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.incomeBudget).isEqualTo(form.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(form.expenseBudget)
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
                assertThat(it.incomeBudget).isEqualTo(domainObject.incomeBudget)
                assertThat(it.expenseBudget).isEqualTo(domainObject.expenseBudget)
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
