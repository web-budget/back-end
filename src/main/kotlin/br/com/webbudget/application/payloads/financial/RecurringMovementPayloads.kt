package br.com.webbudget.application.payloads.financial

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.ErrorCodes.MAX_CHARS
import br.com.webbudget.application.payloads.registration.ClassificationListView
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class RecurringMovementCreateForm(
    @field:Size(message = MAX_CHARS, max = 150)
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    var value: BigDecimal?,
    @field:NotNull(message = IS_NULL)
    var startingAt: LocalDate?,
    @field:NotNull(message = IS_NULL)
    var autoLaunch: Boolean?,
    @field:NotNull(message = IS_NULL)
    var classification: UUID?,
    @field:NotNull(message = IS_NULL)
    var indeterminate: Boolean? = false,
    val totalQuotes: Int? = null,
    val startingQuote: Int? = null,
    val currentQuote: Int? = null,
    val description: String? = null,
)

data class RecurringMovementUpdateForm(
    @field:Size(message = MAX_CHARS, max = 150)
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    var classification: UUID?,
    @field:NotNull(message = IS_NULL)
    var startingAt: LocalDate? = null,
    @field:NotNull(message = IS_NULL)
    var autoLaunch: Boolean? = null,
    val description: String? = null,
)

data class RecurringMovementView(
    val id: UUID,
    val name: String,
    val value: BigDecimal,
    val startingAt: LocalDate,
    val state: String,
    val autoLaunch: Boolean,
    val indeterminate: Boolean,
    val totalQuotes: Int?,
    val startingQuote: Int?,
    val currentQuote: Int?,
    val description: String?,
    val classification: ClassificationListView
)

data class RecurringMovementListView(
    val id: UUID,
    val name: String,
    val value: BigDecimal,
    val state: String,
    val autoLaunch: Boolean,
    val indeterminate: Boolean,
    val totalQuotes: Int?,
    val currentQuote: Int?
)

data class RecurringMovementFilter(
    val filter: String? = null,
    val states: List<String>? = null,
    val costCenter: UUID? = null,
    val classification: UUID? = null
) {
    fun decimalValue(): String? = this.filter?.let {
        it.replace(",", ".").toDoubleOrNull()?.toString()
    }
}
