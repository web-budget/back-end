package br.com.webbudget.domain.entities.financial

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.registration.Wallet
import br.com.webbudget.infrastructure.config.ApplicationSchemas.FINANCIAL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "recurring_movements", schema = FINANCIAL)
class Transference(
    @field:Column(name = "value", nullable = false)
    val value: BigDecimal,
    @field:Column(name = "transferred_at", nullable = false)
    val transferredAt: LocalDate = LocalDate.now(),

    @field:ManyToOne
    @field:JoinColumn(name = "id_from_wallet", nullable = false)
    val fromWallet: Wallet,
    @field:ManyToOne
    @field:JoinColumn(name = "id_to_wallet", nullable = false)
    val toWallet: Wallet,

    @field:Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,
) : PersistentEntity<Long>()