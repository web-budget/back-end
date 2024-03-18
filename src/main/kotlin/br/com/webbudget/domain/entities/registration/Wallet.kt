package br.com.webbudget.domain.entities.registration

import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.UpdateSupport
import br.com.webbudget.infrastructure.config.ApplicationSchemas
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "wallets", schema = ApplicationSchemas.REGISTRATION)
class Wallet(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Enumerated(STRING)
    @field:Column(name = "type", nullable = false)
    var type: Type?,
    @field:Column(name = "current_balance", nullable = false)
    var currentBalance: BigDecimal? = BigDecimal.ZERO,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = true,
    @field:Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,
    @field:Column(name = "bank", length = 150)
    var bank: String? = null,
    @field:Column(name = "agency", length = 10)
    var agency: String? = null,
    @field:Column(name = "number", length = 16)
    var number: String? = null,
) : PersistentEntity<Long>(), UpdateSupport<WalletUpdateForm, Wallet> {

    override fun updateFields(source: WalletUpdateForm): Wallet {
        return this.apply {
            name = source.name
            active = source.active
            description = source.description
            bank = source.bank
            number = source.number
            agency = source.agency
        }
    }

    fun hasValidBankInformation(): Boolean {
        return bank != null && agency != null && number != null // FIXME refactor to use stream
    }

    enum class Type {
        BANK_ACCOUNT, PERSONAL, INVESTMENT
    }
}

