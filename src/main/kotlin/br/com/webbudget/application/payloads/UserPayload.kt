package br.com.webbudget.application.payloads

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserPayload(
    val id: UUID,
    val active: Boolean,
    @field:NotBlank
    val name: String,
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String
)
