package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.CostCenter
import java.util.UUID
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CostCenterForm(
    val active: Boolean = true,
    @field:NotBlank(message = "cost-center.errors.name-is-blank")
    @field:Size(message = "cost-center.errors.name-max-150-chars", max = 150)
    val name: String,
    val description: String?,
)

data class CostCenterView(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val description: String?,
)

data class CostCenterFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<CostCenter> {

    override fun buildPredicates(
        root: Root<CostCenter>,
        query: CriteriaQuery<*>,
        builder: CriteriaBuilder
    ): List<Predicate> {

        val predicates = mutableListOf<Predicate>()

        if (!filter.isNullOrBlank()) {
            predicates.add(
                builder.or(
                    builder.like(builder.lower(root.get("name")), likeIgnoringCase(filter)),
                    builder.like(builder.lower(root.get("description")), likeIgnoringCase(filter))
                )
            )
        }

        if (status != null && status != StatusFilter.ALL) {
            predicates.add(builder.equal(root.get<Boolean>("active"), status.value))
        }

        return predicates
    }
}
