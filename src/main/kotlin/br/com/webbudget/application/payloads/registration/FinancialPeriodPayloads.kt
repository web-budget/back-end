package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ACCOUNTED
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ACTIVE
import br.com.webbudget.domain.entities.registration.FinancialPeriod.Status.ENDED
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository.Specifications.byName
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository.Specifications.byStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class FinancialPeriodCreateForm(
    @field:NotBlank(message = "is-null-or-blank")
    val name: String?,
    @field:NotNull(message = "is-null")
    var startingAt: LocalDate?,
    @field:NotNull(message = "is-null")
    var endingAt: LocalDate?,
    val revenuesGoal: BigDecimal?,
    val expensesGoal: BigDecimal?
)

data class FinancialPeriodUpdateForm(
    @field:NotBlank(message = "is-null-or-blank")
    val name: String?,
    val revenuesGoal: BigDecimal?,
    val expensesGoal: BigDecimal?
)

data class FinancialPeriodView(
    val id: UUID,
    val name: String,
    val startingAt: LocalDate,
    val endingAt: LocalDate,
    val status: String,
    val revenuesGoal: BigDecimal?,
    val expensesGoal: BigDecimal?
)

data class FinancialPeriodListView(
    val id: UUID,
    val name: String,
    val startingAt: LocalDate,
    val endingAt: LocalDate,
    val status: String
)

data class FinancialPeriodFilter(
    val filter: String?,
    val status: String?
) : SpecificationSupport<FinancialPeriod> {

    override fun toSpecification(): Specification<FinancialPeriod> {

        val status = when (this.status) {
            "OPEN" -> listOf(ACTIVE, ENDED)
            "ALL" -> listOf(ACTIVE, ENDED, ACCOUNTED)
            else -> listOf(ACCOUNTED)
        }

        return byName(filter).and(byStatus(status))
    }
}