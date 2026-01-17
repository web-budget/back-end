package br.com.webbudget.application.payloads.administration

import br.com.webbudget.application.payloads.ErrorCodes.EMAIL_IS_INVALID
import br.com.webbudget.application.payloads.ErrorCodes.IS_EMPTY
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL
import br.com.webbudget.application.payloads.ErrorCodes.IS_NULL_OR_BLANK
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
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val name: String?,
    @field:Email(message = EMAIL_IS_INVALID)
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val email: String?,
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    val password: String?,
    @field:NotNull(message = IS_NULL)
    var defaultLanguage: Language?,
    @field:NotEmpty(message = IS_EMPTY)
    val roles: List<String>,
    val sendActivationEmail: Boolean = false
)

data class UserUpdateForm(
    val active: Boolean,
    @field:NotBlank(message = IS_NULL_OR_BLANK)
    var name: String?,
    @field:NotNull(message = IS_NULL)
    var defaultLanguage: Language?,
    @field:NotEmpty(message = IS_EMPTY)
    var roles: List<String>?,
)

data class PasswordChangeForm(
    @field:NotNull(message = IS_NULL)
    var temporary: Boolean,
    @field:NotBlank(message = IS_NULL_OR_BLANK)
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
