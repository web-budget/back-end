package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "cost_centers", schema = ApplicationSchemas.REGISTRATION)
class CostCenter(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = true,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
    @field:Column(name = "income_budget")
    var incomeBudget: BigDecimal? = null,
    @field:Column(name = "expense_budget")
    var expenseBudget: BigDecimal? = null
) : PersistentEntity<Long>()
