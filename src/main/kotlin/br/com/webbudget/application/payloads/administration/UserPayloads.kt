package br.com.webbudget.application.payloads.administration

import br.com.webbudget.application.payloads.SpecificationSupport
import br.com.webbudget.application.payloads.StatusFilter
import br.com.webbudget.domain.entities.administration.Language
import br.com.webbudget.domain.entities.administration.User
import br.com.webbudget.infrastructure.repository.administration.UserRepository.Specifications.byActive
import br.com.webbudget.infrastructure.repository.administration.UserRepository.Specifications.byEmail
import br.com.webbudget.infrastructure.repository.administration.UserRepository.Specifications.byName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

data class UserCreateForm(
    @field:NotBlank(message = "is-null-or-blank")
    val name: String?,
    @field:Email(message = "is-not-valid")
    @field:NotBlank(message = "is-null-or-blank")
    val email: String?,
    @field:NotBlank(message = "is-null-or-blank")
    val password: String?,
    @field:NotNull(message = "is-null")
    val defaultLanguage: Language?,
    @field:NotEmpty(message = "is-empty")
    val authorities: List<String>,
    val sendActivationEmail: Boolean = false
)

data class UserUpdateForm(
    val active: Boolean,
    @field:NotBlank(message = "is-null-or-blank")
    val name: String,
    @field:NotNull(message = "is-null")
    val defaultLanguage: Language,
    @field:NotEmpty(message = "is-empty")
    val authorities: List<String>,
)

data class PasswordChangeForm(
    @field:NotNull(message = "is-null")
    val temporary: Boolean,
    @field:NotBlank(message = "is-null-or-blank")
    val password: String
)

data class UserView(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val email: String,
    val defaultLanguage: Language,
    val authorities: List<String>
)

data class UserFilter(
    val filter: String?,
    val status: StatusFilter?
) : SpecificationSupport<User> {

    override fun toSpecification(): Specification<User> {
        return byActive(status?.value).and(byName(filter).or(byEmail(filter)))
    }
}
