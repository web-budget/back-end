package br.com.webbudget.domain.entities.administration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.DatabaseSchemas.ADMINISTRATION
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "grants", schema = ADMINISTRATION)
class Grant(
    @field:ManyToOne(optional = false)
    @field:JoinColumn(name = "id_user", nullable = false)
    val user: User,
    @field:ManyToOne(optional = false)
    @field:JoinColumn(name = "id_role", nullable = false)
    val role: Role
) : PersistentEntity<Long>()
