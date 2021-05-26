package br.com.webbudget.application.payloads

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class UserDto(
    val id: UUID?,
    @field:NotNull
    val active: Boolean = false,
    @field:NotBlank
    val name: String,
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String,
    @field:NotEmpty
    val roles: List<String>
)

data class UserFilter(
    val name: String,
    val email: String,
    val active: Boolean
)
