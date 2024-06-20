package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.REGISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.stream.Stream

@Entity
@Table(name = "wallets", schema = REGISTRATION)
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
) : PersistentEntity<Long>() {

    fun hasValidBankInformation(): Boolean {
        return Stream.of(bank, agency, number).allMatch { it.isNullOrBlank().not() }
    }

    enum class Type {
        BANK_ACCOUNT, PERSONAL, INVESTMENT
    }
}

