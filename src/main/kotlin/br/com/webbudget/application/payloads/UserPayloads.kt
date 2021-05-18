package br.com.webbudget.application.payloads

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRequest(
    @field:NotBlank
    val name: String,
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String
)

data class UserResponse(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val email: String
)

data class UserFilter(
    val name: String,
    val email: String,
    val active: Boolean
)
