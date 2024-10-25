package br.com.webbudget.application.payloads.financial

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.registration.MovementClassListView
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class ApportionmentForm(
    @field:NotNull(message = IS_NULL)
    val value: BigDecimal,
    @field:NotNull(message = IS_NULL)
    val movementClass: UUID
)

data class ApportionmentView(
    val id: UUID,
    val value: BigDecimal,
    val movementClass: MovementClassListView
)