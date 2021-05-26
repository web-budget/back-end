package br.com.webbudget.application.controllers.administration

import br.com.webbudget.application.payloads.UserDto
import br.com.webbudget.application.payloads.UserFilter
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
    fun get(userFilter: UserFilter, pageable: Pageable): ResponseEntity<Page<UserDto>> {
        val response = userRepository.findByFilter(userFilter, pageable)
            .map { conversionService.convert(it, UserDto::class.java)!! }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<UserDto> {
        return userRepository.findByExternalId(id)
            ?.run { conversionService.convert(this, UserDto::class.java) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun create(@RequestBody userDto: UserDto): ResponseEntity<Any> {

        val toCreate = conversionService.convert(userDto, User::class.java)!!
        val created = userAccountService.createAccount(toCreate, userDto.roles)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody userDto: UserDto): ResponseEntity<UserDto> {

        val toUpdate = conversionService.convert(userDto, User::class.java)!!
        val updated = userAccountService.updateAccount(id, toUpdate)

        return ResponseEntity.ok(conversionService.convert(updated, UserDto::class.java))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        userAccountService.deleteAccount(id)
        return ResponseEntity.ok().build()
    }
}
