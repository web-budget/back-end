package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.ErrorCodes.MAX_CHARS
import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository.Specifications.byDescription
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository.Specifications.byName
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.util.UUID

data class ClassificationCreateForm(
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 150)
    val name: String?,
    @field:NotNull(message = IS_NULL)
    var type: Classification.Type?,
    @field:NotNull(message = IS_NULL)
    var costCenter: UUID?,
    val budget: BigDecimal?,
    val description: String?
)

data class ClassificationUpdateForm(
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

data class ClassificationView(
    val id: UUID,
    val name: String,
    val type: String,
    val active: Boolean,
    val costCenter: CostCenterListView,
    val budget: BigDecimal?,
    val description: String?
)

data class ClassificationListView(
    val id: UUID,
    val name: String,
    val type: String,
    val active: Boolean,
    val costCenter: CostCenterListView,
)

data class ClassificationFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<Classification> {

    override fun toSpecification(): Specification<Classification> {
        return byActive(status?.value).and(byName(filter).or(byDescription(filter)))
    }
}