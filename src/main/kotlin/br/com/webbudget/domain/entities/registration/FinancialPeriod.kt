package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.REGISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "financial_periods", schema = REGISTRATION)
class FinancialPeriod(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "start_date", nullable = false)
    var startDate: LocalDate,
    @field:Column(name = "end_date", nullable = false)
    var endDate: LocalDate,
    @field:Enumerated(EnumType.STRING)
    @field:Column(name = "status", length = 20, nullable = false)
    var status: Status = Status.ACTIVE
) : PersistentEntity<Long>() {

    enum class Status {
        ACTIVE, ENDED, ACCOUNTED
    }
}