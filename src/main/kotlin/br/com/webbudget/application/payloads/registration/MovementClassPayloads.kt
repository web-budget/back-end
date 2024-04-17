package br.com.webbudget.application.payloads.registration

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
    @field:NotBlank(message = "movement-class.errors.name-is-blank")
    @field:Size(message = "movement-class.errors.name-max-150-chars", max = 150)
    val name: String?,
    @field:NotNull(message = "movement-class.errors.type-is-null")
    val type: MovementClass.Type?,
    @field:NotNull(message = "movement-class.errors.cost-center-is-null")
    val costCenter: UUID?,
    val budget: BigDecimal?,
    val description: String?,
    val active: Boolean = true
)

data class MovementClassUpdateForm(
    @field:NotBlank(message = "movement-class.errors.name-is-blank")
    @field:Size(message = "movement-class.errors.name-max-150-chars", max = 150)
    val name: String?,
    @field:NotNull(message = "movement-class.errors.cost-center-is-null")
    val costCenter: UUID?,
    val budget: BigDecimal?,
    val description: String?,
    val active: Boolean = true
)

data class MovementClassView(
    val id: UUID,
    val name: String,
    val type: String,
    val active: Boolean,
    val costCenter: CostCenterView,
    val budget: BigDecimal?,
    val description: String?
)

data class MovementClassFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<MovementClass> {

    override fun toSpecification(): Specification<MovementClass> {
        return byActive(status?.value).and(byName(filter).or(byDescription(filter)))
    }
}