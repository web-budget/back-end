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
    var name: String,
    @Column(name = "email", length = 150, nullable = false)
    var email: String,
    @Column(name = "password", nullable = false)
    var password: String,
    @Column(name = "active", nullable = false)
    var active: Boolean,
    @OneToMany(mappedBy = "user", fetch = EAGER, cascade = [REMOVE])
    var grants: List<Grant>
) : PersistentEntity<Long>() {

    fun prepareForUpdate(user: User) {
        this.name = user.name
        this.email = user.email
        this.active = user.active
    }
}
