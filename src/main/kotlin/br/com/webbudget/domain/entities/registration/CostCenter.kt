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
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = true,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
) : PersistentEntity<Long>(), UpdateSupport<CostCenter> {

    override fun updateFields(source: CostCenter): CostCenter {
        return this.apply {
            name = source.name
            description = source.description
            active = source.active
        }
    }
}