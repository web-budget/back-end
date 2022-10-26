package br.com.webbudget.application.controllers.administration

import br.com.webbudget.application.mappers.configuration.UserMapper
import br.com.webbudget.application.payloads.configuration.UserFilter
import br.com.webbudget.application.payloads.configuration.UserForm
import br.com.webbudget.application.payloads.configuration.UserView
import br.com.webbudget.application.payloads.validation.OnCreateValidation
import br.com.webbudget.application.payloads.validation.OnUpdateValidation
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.administration.UserAccountService
import br.com.webbudget.infrastructure.repository.administration.UserRepository
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
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/administration/users")
class UserController(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val userAccountService: UserAccountService
) {

    @GetMapping
    fun get(filter: UserFilter, pageable: Pageable): ResponseEntity<Page<UserView>> {
        val response = userRepository.findAll(filter.toSpecification(), pageable)
            .map { userMapper.map(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<UserView> {
        return userRepository.findByExternalId(id)
            ?.let { userMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(id)
    }

    @PostMapping
    fun create(@RequestBody @OnCreateValidation userForm: UserForm): ResponseEntity<Any> {

        val toCreate = userMapper.map(userForm)
        val created = userAccountService.createAccount(toCreate, userForm.authorities)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @OnUpdateValidation userForm: UserForm): ResponseEntity<UserView> {

        val toUpdate = userMapper.map(userForm)

        return userRepository.findByExternalId(id)
            ?.updateFields(toUpdate)
            ?.let { userAccountService.updateAccount(it, userForm.authorities) }
            ?.let { ResponseEntity.ok(userMapper.map(it)) }
            ?: throw ResourceNotFoundException(id)
    }

    @PatchMapping("/{id}/update-password")
    fun updatePassword(@PathVariable id: UUID, @RequestBody @NotBlank password: String): ResponseEntity<Any> {

        userRepository.findByExternalId(id)
            ?.let { userAccountService.updatePassword(it, password) }
            ?: throw ResourceNotFoundException(id)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        userRepository.findByExternalId(id)
            ?.let { userAccountService.deleteAccount(it) }
            ?: throw ResourceNotFoundException(id)
        return ResponseEntity.ok().build()
    }
}
