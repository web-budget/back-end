package br.com.webbudget.domain.entities.registration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.REGISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "cards", schema = REGISTRATION)
class Card(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "last_four_digits", length = 4, nullable = false)
    var lastFourDigits: String,
    @field:Enumerated(STRING)
    @field:Column(name = "type", nullable = false, length = 45)
    var type: Type,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = true,
    @field:Column(name = "invoice_payment_day", length = 2)
    var invoicePaymentDay: Int? = null,
    @field:Column(name = "flag", length = 50)
    var flag: String? = null,
    @field:ManyToOne
    @field:JoinColumn(name = "id_wallet")
    var wallet: Wallet? = null
) : PersistentEntity<Long>() {

    enum class Type {
        CREDIT, DEBIT
    }
}

