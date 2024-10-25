package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository.Specifications.byName
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
    val startingAt: LocalDate?,
    @field:NotNull(message = "is-null")
    val endingAt: LocalDate?,
    val revenuesGoal: BigDecimal?,
    val expensesGoal: BigDecimal?
)

data class FinancialPeriodUpdateForm(
    @field:NotBlank(message = "is-null-or-blank")
    val name: String?,
    @field:NotNull(message = "is-null")
    val startingAt: LocalDate?,
    @field:NotNull(message = "is-null")
    val endingAt: LocalDate?,
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
) : SpecificationSupport<FinancialPeriod> {

    override fun toSpecification(): Specification<FinancialPeriod> {
        return byName(filter)
    }
}