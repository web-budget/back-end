package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.REGISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "financial_periods", schema = REGISTRATION)
class FinancialPeriod(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "starting_at", nullable = false)
    var startingAt: LocalDate,
    @field:Column(name = "ending_at", nullable = false)
    var endingAt: LocalDate,
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "status", length = 20, nullable = false)
    var status: Status = Status.ACTIVE,
    @field:Column(name = "expenses_goal")
    var expensesGoal: BigDecimal? = null,
    @field:Column(name = "revenues_goal")
    var revenuesGoal: BigDecimal? = null
) : PersistentEntity<Long>() {

    fun cantBeModified(): Boolean = status != Status.ACTIVE

    enum class Status {
        ACTIVE, ENDED, ACCOUNTED
    }
}