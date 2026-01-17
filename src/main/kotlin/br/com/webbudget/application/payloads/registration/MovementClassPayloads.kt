package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.ErrorCodes.MAX_CHARS
import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository.Specifications.byDescription
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository.Specifications.byName
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.util.UUID

data class MovementClassCreateForm(
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 150)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    var type: MovementClass.Type?,
    @field:NotNull(message = IS_NULL)
    var costCenter: UUID?,
    val budget: BigDecimal?,
    val description: String?
)

data class MovementClassUpdateForm(
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 150)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    var costCenter: UUID?,
    val budget: BigDecimal?,
    val description: String?,
    @field:NotNull(message = IS_NULL)
    var active: Boolean?
)

data class MovementClassView(
    val id: UUID,
    val name: String,
    val type: String,
    val active: Boolean,
    val costCenter: CostCenterListView,
    val budget: BigDecimal?,
    val description: String?
)

data class MovementClassListView(
    val id: UUID,
    val name: String,
    val type: String,
    val active: Boolean,
    val costCenter: CostCenterListView,
)

data class MovementClassFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<MovementClass> {

    override fun toSpecification(): Specification<MovementClass> {
        return byActive(status?.value).and(byName(filter).or(byDescription(filter)))
    }
}