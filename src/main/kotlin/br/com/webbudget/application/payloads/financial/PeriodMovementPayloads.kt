package br.com.webbudget.application.payloads.financial

import br.com.webbudget.application.payloads.ErrorCodes.IS_EMPTY
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.ErrorCodes.MAX_CHARS
import br.com.webbudget.application.payloads.registration.FinancialPeriodListView
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class PeriodMovementCreateForm(
    @field:Size(message = MAX_CHARS, max = 150)
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    val dueDate: LocalDate?,
    @field:NotNull(message = IS_NULL)
    val value: BigDecimal?,
    @field:NotNull(message = IS_NULL)
    val financialPeriod: UUID?,
    val description: String?,
    @field:NotNull(message = IS_NULL)
    @field:NotEmpty(message = IS_EMPTY)
    val apportionments: List<ApportionmentForm>? = emptyList()
)

data class PeriodMovementUpdateForm(
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    val dueDate: LocalDate?,
    @field:NotNull(message = IS_NULL)
    val value: BigDecimal?,
    @field:NotNull(message = IS_NULL)
    val financialPeriod: UUID?,
    val description: String?,
    @field:NotNull(message = IS_NULL)
    @field:NotEmpty(message = IS_EMPTY)
    val apportionments: List<ApportionmentForm>? = emptyList()
)

data class PeriodMovementView(
    val id: UUID,
    val name: String,
    val dueDate: LocalDate,
    val value: BigDecimal,
    val state: String,
    val financialPeriod: FinancialPeriodListView,
    val apportionments: List<ApportionmentView>,
    val quoteNumber: Int? = null,
    val description: String? = null
)

data class PeriodMovementListView(
    val id: UUID,
    val name: String,
    val dueDate: LocalDate,
    val value: BigDecimal,
    val state: String,
    val financialPeriod: FinancialPeriodListView,
)

data class PeriodMovementFilter(
    val filter: String? = null,
    val states: List<String>? = null,
    val costCenter: UUID? = null,
    val movementClass: UUID? = null,
    val financialPeriods: List<UUID>? = null
) {
    fun decimalValue(): String? = this.filter?.let {
        it.replace(",", ".").toDoubleOrNull()?.toString()
    }
}