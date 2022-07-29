package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.UpdateSupport
import br.com.webbudget.infrastructure.config.DefaultSchemas
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "cost_centers", schema = DefaultSchemas.REGISTRATION)
class CostCenter(
    @field:Column(name = "description", length = 150, nullable = false)
    var description: String,
    @field:Column(name = "active", nullable = false)
    var active: Boolean,
) : PersistentEntity<Long>(), UpdateSupport<CostCenter> {

    override fun updateFields(source: CostCenter): CostCenter {
        return this.apply {
            description = source.description
            active = source.active
        }
    }
}