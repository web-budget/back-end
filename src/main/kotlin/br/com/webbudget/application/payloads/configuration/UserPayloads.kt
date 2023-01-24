package br.com.webbudget.application.payloads.configuration

import br.com.webbudget.application.payloads.support.SpecificationSupport
import br.com.webbudget.application.payloads.support.StatusFilter
import br.com.webbudget.domain.entities.administration.User
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class UserCreateForm(
    @field:NotBlank(message = "users.errors.name-is-blank")
    val name: String,
    @field:Email(message = "users.errors.email-is-invalid")
    @field:NotBlank(message = "users.errors.email-is-blank")
    val email: String,
    @field:NotBlank(message = "users.errors.password-is-blank")
    val password: String,
    @field:NotEmpty(message = "users.errors.empty-authorities")
    val authorities: List<String>
)

data class UserUpdateForm(
    val active: Boolean = false,
    @field:NotBlank(message = "users.errors.name-is-blank")
    val name: String,
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
