package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Card.Type
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byFlag
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byLastFourDigits
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byName
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

data class CardCreateForm(
    @field:NotBlank(message = "card.errors.name-is-blank")
    @field:Size(message = "card.errors.name-max-150-chars", max = 150)
    val name: String?,
)

data class CardUpdateForm(
    @field:NotBlank(message = "card.errors.name-is-blank")
    @field:Size(message = "card.errors.name-max-150-chars", max = 150)
    val name: String,
)

data class CardView(
    val id: UUID,
    val name: String,
    val type: Type,
)

data class CardFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<Card> {

    override fun toSpecification(): Specification<Card> {
        return byActive(status?.value).and(byName(filter).or(byLastFourDigits(filter).or(byFlag(filter))))
    }
}
