package br.com.webbudget.domain.entities.administration

import br.com.webbudget.domain.entities.PersistentEntity
import br.com.webbudget.domain.entities.UpdateSupport
import br.com.webbudget.infrastructure.config.DefaultSchemas.ADMINISTRATION
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users", schema = ADMINISTRATION)
class User(
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "email", length = 150, nullable = false)
    var email: String,
    @field:Column(name = "active", nullable = false)
    var active: Boolean = false,
    @field:Column(name = "password", nullable = false)
    var password: String? = null,
    @field:OneToMany(mappedBy = "user", fetch = EAGER, cascade = [REMOVE])
    var grants: List<Grant>? = null
) : PersistentEntity<Long>(), UpdateSupport<User> {

    override fun updateFields(source: User): User {
        this.name = source.name
        this.active = source.active
        return this
    }
}
