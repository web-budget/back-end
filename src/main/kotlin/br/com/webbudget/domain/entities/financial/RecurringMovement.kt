package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.FINANCIAL
import jakarta.persistence.CascadeType.MERGE
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "recurring_movements", schema = FINANCIAL)
class RecurringMovement(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "value", nullable = false)
    var value: BigDecimal,
    @field:Column(name = "starting_at", nullable = false)
    var startingAt: LocalDate,

    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "state", nullable = false, length = 6)
    var state: State = State.ACTIVE,
    @field:Column(name = "auto_launch", nullable = false)
    var autoLaunch: Boolean = true,
    @field:Column(name = "indeterminate", nullable = false)
    var indeterminate: Boolean = false,

    @field:Column(name = "total_quotes")
    var totalQuotes: Int? = null,
    @field:Column(name = "starting_quote")
    var startingQuote: Int? = null,
    @field:Column(name = "current_quote")
    var currentQuote: Int? = null,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @field:OneToMany(mappedBy = "recurringMovement", cascade = [REMOVE, PERSIST, MERGE])
    val apportionments: MutableList<Apportionment> = mutableListOf()
) : PersistentEntity<Long>() {

    /**
     * this is a workaround caused by the way mapstruct works
     *
     * since it is set to target immutable for mapping collections, MS try to find a setter to map the collection
     * items to the target collection
     */
    fun setApportionments(apportionments: List<Apportionment>?) {
        apportionments?.let {
            this.apportionments.clear()
            this.apportionments.addAll(it)
        }
    }

    enum class State {
        ACTIVE, ENDED
    }
}