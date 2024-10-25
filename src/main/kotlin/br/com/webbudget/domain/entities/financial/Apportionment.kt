package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.infrastructure.config.ApplicationSchemas.FINANCIAL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "apportionments", schema = FINANCIAL)
class Apportionment(
    @field:Column(name = "value", nullable = false)
    val value: BigDecimal,

    @field:ManyToOne
    @field:JoinColumn(name = "id_movement_class", nullable = false)
    val movementClass: MovementClass,

    @field:ManyToOne
    @field:JoinColumn(name = "id_period_movement")
    var periodMovement: PeriodMovement? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "id_recurring_movement")
    var recurringMovement: RecurringMovement? = null
) : PersistentEntity<Long>()