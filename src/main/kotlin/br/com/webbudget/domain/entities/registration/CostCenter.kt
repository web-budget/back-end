package br.com.webbudget.domain.entities.registration

import br.com.webbudget.application.payloads.registration.CostCenterForm
import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.UpdateSupport
import br.com.webbudget.infrastructure.config.DefaultSchemas
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "cost_centers", schema = DefaultSchemas.REGISTRATION)
class CostCenter(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = true,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
) : PersistentEntity<Long>(), UpdateSupport<CostCenterForm, CostCenter> {

    override fun updateFields(source: CostCenterForm): CostCenter {
        return this.apply {
            name = source.name
            description = source.description
            active = source.active
        }
    }
}
