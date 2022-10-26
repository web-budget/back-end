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
    fun `should map cost center form to cost center`() {

        val costCenterForm = CostCenterForm(true, "Cost Center", "Some cost center")

        val costCenter = costCenterMapper.map(costCenterForm)

        assertThat(costCenter)
            .isNotNull
            .hasFieldOrPropertyWithValue("active", costCenterForm.active)
            .hasFieldOrPropertyWithValue("name", costCenterForm.name)
            .hasFieldOrPropertyWithValue("description", costCenterForm.description)
    }

    @Test
    fun `should map cost center to cost center view`() {

        val externalId = UUID.randomUUID()
        val costCenter = CostCenter("Cost Center", true, "Some cost center")
            .apply {
                this.id = 1L
                this.externalId = externalId
            }

        val costCenterView = costCenterMapper.map(costCenter)

        assertThat(costCenterView)
            .isNotNull
            .hasFieldOrPropertyWithValue("id", externalId)
            .hasFieldOrPropertyWithValue("active", costCenter.active)
            .hasFieldOrPropertyWithValue("name", costCenter.name)
            .hasFieldOrPropertyWithValue("description", costCenter.description)
    }
}