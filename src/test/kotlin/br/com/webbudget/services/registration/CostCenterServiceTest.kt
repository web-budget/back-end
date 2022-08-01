package br.com.webbudget.services.registration

import br.com.webbudget.TestRunner
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.services.registration.CostCenterService
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CostCenterServiceTest : TestRunner() {

    @Autowired
    private lateinit var costCenterService: CostCenterService

    @Test
    fun `should create`() {

        val toCreate = CostCenter("New cost center", true)

        val created = costCenterService.create(toCreate)

        assertThat(created).isEqualTo(toCreate)
    }

    @Test
    fun `should fail if description is duplicated`() {

        val toCreate = CostCenter("Some cost center", true)
        costCenterService.create(toCreate)

        val duplicated = CostCenter("Some cost center", true)

        AssertionsForClassTypes.assertThatThrownBy { costCenterService.create(duplicated) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("cost-center.description")
    }

    @Test
    fun `should update`() {

        val toCreate = CostCenter("Another cost center", true)
        val created = costCenterService.create(toCreate)

        created.active = false

        val updated = costCenterService.update(created)

        assertThat(updated)
            .isEqualTo(created)
            .hasFieldOrPropertyWithValue("active", false)
    }
}