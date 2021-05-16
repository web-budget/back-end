package br.com.webbudget.domain.entities.configuration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.DefaultSchemas.ADMINISTRATION
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "grants", schema = ADMINISTRATION)
class Grant(
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    val user: User,
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_authority", nullable = false)
    val authority: Authority
) : PersistentEntity<Long>()