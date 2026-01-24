package br.com.webbudget.application.payloads.financial

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.ErrorCodes.MAX_CHARS
import br.com.webbudget.application.payloads.registration.ClassificationListView
import br.com.webbudget.application.payloads.registration.FinancialPeriodListView
import jakarta.validation.constraints.NotBlank
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
    var dueDate: LocalDate?,
    @field:NotNull(message = IS_NULL)
    var value: BigDecimal?,
    @field:NotNull(message = IS_NULL)
    var financialPeriod: UUID?,
    @field:NotNull(message = IS_NULL)
    var classification: UUID?,
    val description: String?,
)

data class PeriodMovementUpdateForm(
    @field:Size(message = MAX_CHARS, max = 150)
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val name: String? = null,
    @field:NotNull(message = IS_NULL)
    var dueDate: LocalDate? = null,
    @field:NotNull(message = IS_NULL)
    var value: BigDecimal? = null,
    @field:NotNull(message = IS_NULL)
    var financialPeriod: UUID? = null,
    @field:NotNull(message = IS_NULL)
    var classification: UUID? = null,
    val description: String? = null,
)

data class PeriodMovementView(
    val id: UUID,
    val name: String,
    val dueDate: LocalDate,
    val value: BigDecimal,
    val state: String,
    val classification: ClassificationListView,
    val financialPeriod: FinancialPeriodListView,
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
    val classification: UUID? = null,
    val financialPeriods: List<UUID>? = null
) {
    fun decimalValue(): String? = this.filter?.let {
        it.replace(",", ".").toDoubleOrNull()?.toString()
    }
}