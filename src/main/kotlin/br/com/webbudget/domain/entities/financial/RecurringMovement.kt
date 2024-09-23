package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.ADMINISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "recurring_movements", schema = ADMINISTRATION)
class RecurringMovement(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "value", nullable = false)
    var value: BigDecimal,
    @field:Column(name = "total_quotes", nullable = false)
    var totalQuotes: Int,
    @field:Column(name = "starting_on", nullable = false)
    val startingOn: LocalDate,

    @field:Column(name = "starting_quote", nullable = false)
    var startingQuote: Int = 1,
    @field:Column(name = "current_quote", nullable = false)
    var currentQuote: Int = 1,
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "state", nullable = false, length = 9)
    var state: State = State.ACTIVE,
    @field:Column(name = "auto_launch", nullable = false)
    var autoLaunch: Boolean = true,
    @field:Column(name = "indeterminate", nullable = false)
    var indeterminate: Boolean = false,

    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
) : PersistentEntity<Long>() {

    enum class State {
        ACTIVE, ENDED
    }
}