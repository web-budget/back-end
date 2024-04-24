package br.com.webbudget.application.controllers.administration

import br.com.webbudget.application.mappers.configuration.UserMapper
import br.com.webbudget.application.payloads.administration.PasswordChangeForm
import br.com.webbudget.application.payloads.administration.UserCreateForm
import br.com.webbudget.application.payloads.administration.UserFilter
import br.com.webbudget.application.payloads.administration.UserUpdateForm
import br.com.webbudget.application.payloads.administration.UserView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.administration.UserService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/administration/users")
class UserController(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val userService: UserService
) {

    @GetMapping
    fun get(filter: UserFilter, pageable: Pageable): ResponseEntity<Page<UserView>> {
        return userRepository.findAll(filter.toSpecification(), pageable)
            .map { userMapper.map(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<UserView> {
        return userRepository.findByExternalId(id)
            ?.let { userMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("userId" to id))
    }

    @PostMapping
    fun create(@RequestBody @Valid form: UserCreateForm): ResponseEntity<Any> {

        val toCreate = userMapper.map(form)
        val created = userService.createAccount(toCreate, form.authorities, form.sendActivationEmail)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid form: UserUpdateForm): ResponseEntity<UserView> {

        val user = userRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException(mapOf("userId" to id))

        userMapper.map(form, user)
        userService.updateAccount(user, form.authorities)

        return ResponseEntity.ok(userMapper.map(user))
    }

    @PatchMapping("/{id}/update-password")
    fun updatePassword(@PathVariable id: UUID, @RequestBody @Valid form: PasswordChangeForm): ResponseEntity<Any> {

        val (temporary, password) = form

        userRepository.findByExternalId(id)
            ?.let { userService.updatePassword(it, password, temporary) }
            ?: throw ResourceNotFoundException(mapOf("userId" to id))

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        userRepository.findByExternalId(id)
            ?.let { userService.deleteAccount(it) }
            ?: throw ResourceNotFoundException(mapOf("userId" to id))
        return ResponseEntity.ok().build()
    }
}
