package br.com.webbudget.application.controllers.administration

import br.com.webbudget.application.payloads.UserFilter
import br.com.webbudget.application.payloads.UserRequest
import br.com.webbudget.application.payloads.UserResponse
import br.com.webbudget.domain.entities.configuration.User
import br.com.webbudget.domain.services.UserAccountService
import br.com.webbudget.infrastructure.repository.configuration.UserRepository
import org.springframework.core.convert.ConversionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository,
    private val conversionService: ConversionService,
    private val userAccountService: UserAccountService
) {

    @GetMapping
    fun get(userFilter: UserFilter, pageable: Pageable): ResponseEntity<Page<UserResponse>> {
        val response = userRepository.findByFilter(userFilter, pageable)
            .map { conversionService.convert(it, UserResponse::class.java)!! }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        return userRepository.findByExternalId(id)
            .let { conversionService.convert(it, UserResponse::class.java) }
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    fun create(@RequestBody userRequest: UserRequest): ResponseEntity<Any> {

        val created = userAccountService.createAccount(conversionService.convert(userRequest, User::class.java)!!)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {

        val updatable = conversionService.convert(userRequest, User::class.java)!!
        val updated = userAccountService.updateAccount(id, updatable)

        return ResponseEntity.ok(conversionService.convert(updated, UserResponse::class.java))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        userAccountService.deleteAccount(id)
        return ResponseEntity.ok().build()
    }
}
