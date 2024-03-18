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
@Table(name = "password_recover_attempts", schema = ADMINISTRATION)
class PasswordRecoverAttempt(
    @field:Column(name = "token", nullable = false)
    var token: UUID,
    @field:ManyToOne(optional = false)
    @field:JoinColumn(name = "id_user", nullable = false)
    val user: User,
    @field:Column(name = "used", nullable = false)
    var used: Boolean = false,
    @field:Column(name = "valid_until", nullable = false)
    var validity: LocalDateTime = LocalDateTime.now().plusHours(VALIDITY_TIME_LIMIT_HOURS),
) : PersistentEntity<Long>() {

    companion object {
        private const val VALIDITY_TIME_LIMIT_HOURS = 3L
    }
}
