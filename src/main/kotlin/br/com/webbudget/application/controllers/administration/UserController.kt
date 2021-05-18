package br.com.webbudget.application.controllers.administration

import br.com.webbudget.application.payloads.UserPayload
import br.com.webbudget.domain.services.UserAccountService
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.springframework.core.convert.ConversionService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository,
    private val conversionService: ConversionService,
    private val userAccountService: UserAccountService
) {

    @GetMapping
    fun get(): ResponseEntity<Page<UserPayload>> {
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<UserPayload> {
        return ResponseEntity.ok().build()
    }

    @PostMapping
    fun create(@RequestBody userPayload: UserPayload): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody userPayload: UserPayload): ResponseEntity<UserPayload> {
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }
}
