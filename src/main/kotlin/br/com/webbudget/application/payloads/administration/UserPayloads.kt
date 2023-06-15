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
    @field:NotBlank(message = "users.errors.name-is-blank")
    val name: String?,
    @field:Email(message = "users.errors.email-is-invalid")
    @field:NotBlank(message = "users.errors.email-is-blank")
    val email: String?,
    @field:NotBlank(message = "users.errors.password-is-blank")
    val password: String?,
    @field:NotNull(message = "users.errors.null-default-language")
    val defaultLanguage: Language?,
    @field:NotEmpty(message = "users.errors.empty-authorities")
    val authorities: List<String>,
    val sendActivationEmail: Boolean = false
)

data class UserUpdateForm(
    val active: Boolean,
    @field:NotBlank(message = "users.errors.name-is-blank")
    val name: String,
    @field:NotNull(message = "users.errors.null-default-language")
    val defaultLanguage: Language,
    @field:NotEmpty(message = "users.errors.empty-authorities")
    val authorities: List<String>,
)

data class PasswordChangeForm(
    @field:NotNull(message = "users.errors.temporary-is-required")
    val temporary: Boolean,
    @field:NotBlank(message = "users.errors.password-is-blank")
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
