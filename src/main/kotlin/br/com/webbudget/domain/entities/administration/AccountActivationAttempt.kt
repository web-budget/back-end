package br.com.webbudget.domain.entities.administration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.ADMINISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "account_activation_attempt", schema = ADMINISTRATION)
class AccountActivationAttempt(
    @field:Column(name = "token", nullable = false)
    var token: UUID,
    @field:ManyToOne(optional = false)
    @field:JoinColumn(name = "id_user", nullable = false)
    val user: User,
    @field:Column(name = "activated_on")
    var activatedOn: LocalDateTime? = null,
    @field:Column(name = "valid_until", nullable = false)
    var validity: LocalDateTime = LocalDateTime.now().plusDays(VALIDITY_TIME_LIMIT_DAYS),
) : PersistentEntity<Long>() {

    companion object {
        private const val VALIDITY_TIME_LIMIT_DAYS = 5L
    }
}
