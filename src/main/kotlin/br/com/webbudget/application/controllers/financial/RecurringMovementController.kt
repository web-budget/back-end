package br.com.webbudget.application.controllers.financial

import br.com.webbudget.application.mappers.financial.RecurringMovementMapper
import br.com.webbudget.application.payloads.financial.RecurringMovementCreateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementFilter
import br.com.webbudget.application.payloads.financial.RecurringMovementListView
import br.com.webbudget.application.payloads.financial.RecurringMovementUpdateForm
import br.com.webbudget.application.payloads.financial.RecurringMovementView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.financial.RecurringMovementService
import br.com.webbudget.infrastructure.repository.financial.RecurringMovementRepository
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
@RequestMapping("/api/financial/recurring-movements")
class RecurringMovementController(
    private val recurringMovementMapper: RecurringMovementMapper,
    private val recurringMovementService: RecurringMovementService,
    private val recurringMovementRepository: RecurringMovementRepository
) {

    @GetMapping
    fun get(filter: RecurringMovementFilter, pageable: Pageable): ResponseEntity<Page<RecurringMovementListView>> =
        recurringMovementRepository.findByFilter(filter, pageable)
            .map { recurringMovementMapper.mapToListView(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<RecurringMovementView> =
        recurringMovementRepository.findByExternalId(id)
            ?.let { recurringMovementMapper.mapToView(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException()

    @PostMapping
    fun create(@RequestBody @Valid form: RecurringMovementCreateForm): ResponseEntity<Any> {

        val toCreate = recurringMovementMapper.mapToDomain(form)
        val created = recurringMovementService.create(toCreate)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: RecurringMovementUpdateForm
    ): ResponseEntity<RecurringMovementListView> {

        val periodMovement = recurringMovementRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException()

        recurringMovementMapper.mapToDomain(form, periodMovement)
        recurringMovementService.update(periodMovement)

        return ResponseEntity.ok(recurringMovementMapper.mapToListView(periodMovement))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> =
        recurringMovementRepository.findByExternalId(id)
            ?.let { recurringMovementService.delete(it) }
            ?.let { ResponseEntity.ok().build() }
            ?: throw ResourceNotFoundException()
}