package br.com.webbudget.backend.domain.entities.configuration

import br.com.webbudget.backend.domain.entities.PersistentEntity
import br.com.webbudget.backend.infrastructure.config.DefaultSchemas
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "authorities", schema = DefaultSchemas.CONFIGURATION)
class Authority(
    @Column(name = "name", nullable = false, length = 45)
    val name: String
) : PersistentEntity<Long>() {
}