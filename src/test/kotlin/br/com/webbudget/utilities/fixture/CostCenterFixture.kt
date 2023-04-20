package br.com.webbudget.utilities.fixture

import br.com.webbudget.domain.entities.registration.CostCenter
import java.util.UUID

object CostCenterFixture {

    fun create(id: Long, externalId: UUID) = create().apply {
        this.id = id
        this.externalId = externalId
    }

    fun create() = CostCenter("Cost Center", true, "Something to describe")
}
