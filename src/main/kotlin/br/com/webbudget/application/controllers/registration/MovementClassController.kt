package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.mappers.registration.MovementClassMapper
import br.com.webbudget.application.payloads.registration.MovementClassCreateForm
import br.com.webbudget.application.payloads.registration.MovementClassFilter
import br.com.webbudget.application.payloads.registration.MovementClassUpdateForm
import br.com.webbudget.application.payloads.registration.MovementClassView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.registration.MovementClassService
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import jakarta.validation.Valid
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
@RequestMapping("/api/registration/movement-classes")
class MovementClassController(
    private val movementClassMapper: MovementClassMapper,
    private val movementClassService: MovementClassService,
    private val movementClassRepository: MovementClassRepository
) {

    @GetMapping
    fun get(filter: MovementClassFilter, pageable: Pageable): ResponseEntity<Page<MovementClassView>> =
        movementClassRepository.findAll(filter.toSpecification(), pageable)
            .map { movementClassMapper.map(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<MovementClassView> =
        movementClassRepository.findByExternalId(id)
            ?.let { movementClassMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("movementClassId" to id))

    @PostMapping
    fun create(@RequestBody @Valid form: MovementClassCreateForm): ResponseEntity<Any> {

        val movementClass = movementClassMapper.map(form)
        val created = movementClassService.create(movementClass)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: MovementClassUpdateForm
    ): ResponseEntity<MovementClassView> {

        val movementClass = movementClassRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException(mapOf("movementClassId" to id))

        movementClassMapper.map(form, movementClass)
        movementClassService.update(movementClass)

        return ResponseEntity.ok(movementClassMapper.map(movementClass))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> = movementClassRepository.findByExternalId(id)
        ?.let { movementClassService.delete(it) }
        ?.let { ResponseEntity.ok().build() }
        ?: throw ResourceNotFoundException(mapOf("movementClassId" to id))
}