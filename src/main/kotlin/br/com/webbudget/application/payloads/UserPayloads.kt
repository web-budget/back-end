package br.com.webbudget.application.payloads

import br.com.webbudget.application.payloads.validation.OnCreate
import br.com.webbudget.domain.entities.configuration.User
import java.util.UUID
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class UserForm(
    val id: UUID?,
    val active: Boolean = false,
    @field:NotBlank(message = "user.name.is-blank")
    val name: String,
    @field:Email(message = "user.email.is-invalid")
    @field:NotBlank(message = "user.email.is-blank")
    val email: String,
    @field:NotBlank(message = "user.password.is-blank", groups = [OnCreate::class])
    val password: String?,
    @field:NotEmpty(message = "user.roles.is-empty")
    val roles: List<String>
)

data class UserView(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val email: String,
    val roles: List<String>
)

data class UserFilter(
    val filter: String?,
    val active: Boolean?
) : SpecificationSupport<User> {

    override fun buildPredicates(root: Root<User>, query: CriteriaQuery<*>, builder: CriteriaBuilder): List<Predicate> {

        val predicates = mutableListOf<Predicate>()

        if (!filter.isNullOrBlank()) {
            predicates.add(
                builder.or(
                    builder.like(builder.lower(root.get("name")), filter.lowercase()),
                    builder.like(builder.lower(root.get("email")), filter.lowercase())
                )
            )
        }

        if (active != null) {
            predicates.add(builder.equal(root.get<Boolean>("active"), active))
        }

        return predicates
    }
}
