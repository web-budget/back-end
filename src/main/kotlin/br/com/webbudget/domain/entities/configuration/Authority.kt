package br.com.webbudget.domain.entities.configuration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.DefaultSchemas.ADMINISTRATION
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "authorities", schema = ADMINISTRATION)
class Authority(
    @field:Column(name = "name", nullable = false, length = 45)
    val name: String
) : PersistentEntity<Long>()
