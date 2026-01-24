package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.payloads.registration.ClassificationCreateForm
import br.com.webbudget.application.payloads.registration.ClassificationFilter
import br.com.webbudget.application.payloads.registration.ClassificationListView
import br.com.webbudget.application.payloads.registration.ClassificationView
import br.com.webbudget.application.payloads.registration.ClassificationUpdateForm
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.registration.ClassificationService
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
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
@RequestMapping("/api/registration/classifications")
class ClassificationController(
    private val classificationMapper: ClassificationMapper,
    private val classificationService: ClassificationService,
    private val classificationRepository: ClassificationRepository
) {

    @GetMapping
    fun get(filter: ClassificationFilter, pageable: Pageable): ResponseEntity<Page<ClassificationListView>> =
        classificationRepository.findAll(filter.toSpecification(), pageable)
            .map { classificationMapper.mapToListView(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<ClassificationView> =
        classificationRepository.findByExternalId(id)
            ?.let { classificationMapper.mapToView(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException()

    @PostMapping
    fun create(@RequestBody @Valid form: ClassificationCreateForm): ResponseEntity<Any> {

        val movementClass = classificationMapper.mapToDomain(form)
        val created = classificationService.create(movementClass)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: ClassificationUpdateForm
    ): ResponseEntity<ClassificationView> {

        val movementClass = classificationRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException()

        classificationMapper.mapToDomain(form, movementClass)
        classificationService.update(movementClass)

        return ResponseEntity.ok(classificationMapper.mapToView(movementClass))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> = classificationRepository.findByExternalId(id)
        ?.let { classificationService.delete(it) }
        ?.let { ResponseEntity.ok().build() }
        ?: throw ResourceNotFoundException()
}