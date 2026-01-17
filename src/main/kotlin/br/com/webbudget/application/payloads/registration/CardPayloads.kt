package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
import br.com.webbudget.application.payloads.ErrorCodes.MAX_CHARS
import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.Card
import br.com.webbudget.domain.entities.registration.Card.Type
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byFlag
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byLastFourDigits
import br.com.webbudget.infrastructure.repository.registration.CardRepository.Specifications.byName
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

data class CardCreateForm(
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 150)
    val name: String?,
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 4)
    val lastFourDigits: String?,
    val invoicePaymentDay: Int?,
    @field:NotNull(message = IS_NULL)
    var type: Type?,
    val wallet: UUID?,
    val flag: String?
)

data class CardUpdateForm(
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 150)
    val name: String?,
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    @field:Size(message = MAX_CHARS, max = 4)
    val lastFourDigits: String?,
    val invoicePaymentDay: Int?,
    val wallet: UUID?,
    val flag: String?,
    @field:NotNull(message = IS_NULL)
    var active: Boolean?
)

data class CardView(
    val id: UUID,
    val name: String,
    val lastFourDigits: String,
    val type: Type,
    val active: Boolean,
    val flag: String? = null,
    val wallet: WalletListView? = null,
    val invoicePaymentDay: Int? = null,
)

data class CardListView(
    val id: UUID,
    val name: String,
    val type: Type,
    val active: Boolean,
    val flag: String? = null,
)

data class CardFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<Card> {

    override fun toSpecification(): Specification<Card> {
        return byActive(status?.value).and(byName(filter).or(byLastFourDigits(filter).or(byFlag(filter))))
    }
}
