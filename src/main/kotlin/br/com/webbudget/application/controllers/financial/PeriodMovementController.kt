package br.com.webbudget.application.controllers.financial

import br.com.webbudget.application.mappers.financial.PeriodMovementMapper
import br.com.webbudget.application.payloads.financial.PeriodMovementCreateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementFilter
import br.com.webbudget.application.payloads.financial.PeriodMovementUpdateForm
import br.com.webbudget.application.payloads.financial.PeriodMovementView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.financial.PeriodMovementService
import br.com.webbudget.infrastructure.repository.financial.PeriodMovementRepository
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
@RequestMapping("/api/financial/period-movements")
class PeriodMovementController(
    private val periodMovementMapper: PeriodMovementMapper,
    private val periodMovementService: PeriodMovementService,
    private val periodMovementRepository: PeriodMovementRepository
) {

    @GetMapping
    fun get(filter: PeriodMovementFilter, pageable: Pageable): ResponseEntity<Page<PeriodMovementView>> =
        periodMovementRepository.findByFilter(filter, pageable)
            .map { periodMovementMapper.map(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<PeriodMovementView> =
        periodMovementRepository.findByExternalId(id)
            ?.let { periodMovementMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("periodMovementId" to id))

    @PostMapping
    fun create(@RequestBody @Valid form: PeriodMovementCreateForm): ResponseEntity<Any> {

        val toCreate = periodMovementMapper.map(form)
        val created = periodMovementService.create(toCreate)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: PeriodMovementUpdateForm
    ): ResponseEntity<PeriodMovementView> {

        val periodMovement = periodMovementRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException(mapOf("periodMovementId" to id))

        periodMovementMapper.map(form, periodMovement)
        periodMovementService.update(periodMovement)

        return ResponseEntity.ok(periodMovementMapper.map(periodMovement))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> =
        periodMovementRepository.findByExternalId(id)
            ?.let { periodMovementService.delete(it) }
            ?.let { ResponseEntity.ok().build() }
            ?: throw ResourceNotFoundException(mapOf("periodMovementId" to id))
}