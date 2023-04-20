package br.com.webbudget.domain.entities.administration

import br.com.webbudget.application.payloads.administration.UserUpdateForm
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
    @field:Column(name = "active", nullable = false)
    var active: Boolean = false,
    @field:Column(name = "name", length = 150, nullable = false)
    var name: String,
    @field:Column(name = "email", length = 150, nullable = false)
    var email: String? = null,
    @field:Column(name = "password", nullable = false)
    var password: String? = null,
    @field:OneToMany(mappedBy = "user", fetch = EAGER, cascade = [REMOVE])
    var grants: List<Grant>? = null
) : PersistentEntity<Long>(), UpdateSupport<UserUpdateForm, User> {

    fun isAdmin(): Boolean {
        return this.email == ADMIN_USERNAME
    }

    override fun updateFields(source: UserUpdateForm): User {
        return this.apply {
            name = source.name
            active = source.active
        }
    }

    companion object {
        private const val ADMIN_USERNAME = "admin@webbudget.com.br"
    }
}
