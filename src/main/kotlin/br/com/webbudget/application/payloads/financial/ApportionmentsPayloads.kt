package br.com.webbudget.application.payloads.financial

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.Views
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.application.payloads.registration.MovementClassView
import com.fasterxml.jackson.annotation.JsonView
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class ApportionmentForm(
    @field:NotNull(message = IS_NULL)
    val value: BigDecimal,
    @field:NotNull(message = IS_NULL)
    val movementClass: UUID,
    @field:NotNull(message = IS_NULL)
    val costCenter: UUID
)

data class ApportionmentView(
    @JsonView(Views.Minimal::class)
    val id: UUID,
    @JsonView(Views.Minimal::class)
    val value: BigDecimal,
    val movementClass: MovementClassView,
    val costCenter: CostCenterView,
    val periodMovementView: PeriodMovementView
)