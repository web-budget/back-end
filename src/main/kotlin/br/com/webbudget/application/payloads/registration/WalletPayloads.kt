package br.com.webbudget.application.payloads.registration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.domain.entities.registration.Wallet.Type
import br.com.webbudget.infrastructure.repository.registration.WalletRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.registration.WalletRepository.Specifications.byAgency
import br.com.webbudget.infrastructure.repository.registration.WalletRepository.Specifications.byBankName
import br.com.webbudget.infrastructure.repository.registration.WalletRepository.Specifications.byDescription
import br.com.webbudget.infrastructure.repository.registration.WalletRepository.Specifications.byName
import br.com.webbudget.infrastructure.repository.registration.WalletRepository.Specifications.byNumber
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.util.UUID

data class WalletCreateForm(
    @field:NotBlank(message = "is-null-or-blank")
    @field:Size(message = "max-150-chars", max = 150)
    val name: String?,
    @field:NotNull(message = "is-null")
    var type: Type?,
    val description: String? = null,
    val bank: String? = null,
    val agency: String? = null,
    val number: String? = null,
)

data class WalletUpdateForm(
    @field:NotBlank(message = "is-null-or-blank")
    @field:Size(message = "max-150-chars", max = 150)
    val name: String,
    val active: Boolean,
    val description: String? = null,
    @field:Size(message = "max-150-chars", max = 150)
    val bank: String? = null,
    @field:Size(message = "max-10-chars", max = 10)
    val agency: String? = null,
    @field:Size(message = "max-16-chars", max = 16)
    val number: String? = null,
)

data class WalletView(
    val id: UUID,
    val name: String,
    val type: Type,
    val currentBalance: BigDecimal,
    val active: Boolean = true,
    val description: String? = null,
    val bank: String? = null,
    val number: String? = null,
    val agency: String? = null,
)

data class WalletListView(
    val id: UUID,
    val name: String,
    val type: Type,
    val active: Boolean = true
)

data class WalletFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<Wallet> {

    override fun toSpecification(): Specification<Wallet> {
        return byActive(status?.value).and(
            byName(filter).or(byDescription(filter).or(byBankName(filter).or(byAgency(filter).or(byNumber(filter)))))
        )
    }
}
