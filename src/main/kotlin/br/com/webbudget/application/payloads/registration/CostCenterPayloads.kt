package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository.Specifications.byDescription
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository.Specifications.byName
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.util.UUID

data class CostCenterCreateForm(
    @field:NotBlank(message = "is-null-or-blank")
    @field:Size(message = "max-150-chars", max = 150)
    val name: String?,
    val active: Boolean = true,
    val description: String? = null,
    val incomeBudget: BigDecimal? = null,
    val expenseBudget: BigDecimal? = null
)

data class CostCenterUpdateForm(
    val name: String?,
    val active: Boolean? = true,
    val description: String?,
    val incomeBudget: BigDecimal? = null,
    val expenseBudget: BigDecimal? = null
)

data class CostCenterView(
    val id: UUID,
    val name: String,
    val active: Boolean,
    val description: String? = null,
    val incomeBudget: BigDecimal? = null,
    val expenseBudget: BigDecimal? = null
)

data class CostCenterListView(
    val id: UUID,
    val name: String,
    val active: Boolean,
    val incomeBudget: BigDecimal? = null,
    val expenseBudget: BigDecimal? = null
)

data class CostCenterFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<CostCenter> {

    override fun toSpecification(): Specification<CostCenter> {
        return byActive(status?.value).and(byName(filter).or(byDescription(filter)))
    }
}
