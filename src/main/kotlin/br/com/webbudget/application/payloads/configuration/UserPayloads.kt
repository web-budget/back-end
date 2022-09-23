package br.com.webbudget.application.payloads.configuration

import br.com.webbudget.application.payloads.support.SpecificationSupport
import br.com.webbudget.application.payloads.support.StatusFilter
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
    val active: Boolean = false,
    @field:NotBlank(message = "users.errors.name-is-blank")
    val name: String,
    @field:Email(message = "users.errors.email-is-invalid")
    @field:NotBlank(message = "users.errors.email-is-blank")
    val email: String,
    @field:NotBlank(message = "users.errors.password-is-blank", groups = [OnCreate::class])
    val password: String?,
    @field:NotEmpty(message = "users.errors.empty-authorities")
    val authorities: List<String>
)

data class UserView(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val email: String,
    val authorities: List<String>
)

data class UserFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<User> {

    override fun buildPredicates(root: Root<User>, query: CriteriaQuery<*>, builder: CriteriaBuilder): List<Predicate> {

        val predicates = mutableListOf<Predicate>()

        if (!filter.isNullOrBlank()) {
            predicates.add(
                builder.or(
                    builder.like(builder.lower(root.get("name")), likeIgnoringCase(filter)),
                    builder.like(builder.lower(root.get("email")), likeIgnoringCase(filter))
                )
            )
        }

        if (status != null) {
            predicates.add(builder.equal(root.get<Boolean>("active"), status.value))
        }

        return predicates
    }
}
