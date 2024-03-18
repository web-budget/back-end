package br.com.webbudget.domain.entities.administration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.ApplicationSchemas.ADMINISTRATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "authorities", schema = ADMINISTRATION)
class Authority(
    @field:Column(name = "name", nullable = false, length = 45)
    val name: String
) : PersistentEntity<Long>()
