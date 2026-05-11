package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.DatabaseSchemas.REGISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "classifications", schema = REGISTRATION)
class Classification(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "type", length = 45, nullable = false)
    var type: Type,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = true,
    @field:Column(name = "budget")
    var budget: BigDecimal? = null,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
) : PersistentEntity<Long>() {

    enum class Type {
        INCOME, EXPENSE
    }
}