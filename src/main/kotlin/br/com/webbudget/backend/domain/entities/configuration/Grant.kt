package br.com.webbudget.backend.domain.entities.configuration

import br.com.webbudget.backend.domain.entities.PersistentEntity
import br.com.webbudget.backend.infrastructure.config.DefaultSchemas
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "grants", schema = DefaultSchemas.CONFIGURATION)
class Grant(
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    val user: User,
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_authority", nullable = false)
    val authority: Authority
) : PersistentEntity<Long>()