package br.com.webbudget.domain.entities.configuration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.DefaultSchemas.CONFIGURATION
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "authorities", schema = CONFIGURATION)
class Authority(
    @Column(name = "name", nullable = false, length = 45)
    val name: String
) : PersistentEntity<Long>()