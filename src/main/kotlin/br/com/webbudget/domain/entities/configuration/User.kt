package br.com.webbudget.domain.entities.configuration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.infrastructure.config.DefaultSchemas.ADMINISTRATION
import javax.persistence.CascadeType.REMOVE
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "users", schema = ADMINISTRATION)
class User(
    @Column(name = "name", length = 150, nullable = false)
    val name: String,
    @Column(name = "email", length = 150, nullable = false)
    val email: String,
    @Column(name = "password", nullable = false)
    var password: String,
    @Column(name = "active", nullable = false)
    val active: Boolean,
    @OneToMany(mappedBy = "user", fetch = EAGER, cascade = [REMOVE])
    val grants: List<Grant>
) : PersistentEntity<Long>()
