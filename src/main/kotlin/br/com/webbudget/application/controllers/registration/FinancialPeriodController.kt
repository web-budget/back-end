package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.mappers.registration.FinancialPeriodMapper
import br.com.webbudget.application.payloads.registration.FinancialPeriodCreateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodFilter
import br.com.webbudget.application.payloads.registration.FinancialPeriodUpdateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodView
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.registration.FinancialPeriodService
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
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
@RequestMapping("/api/registration/financial-periods")
class FinancialPeriodController(
    private val financialPeriodMapper: FinancialPeriodMapper,
    private val financialPeriodService: FinancialPeriodService,
    private val financialPeriodRepository: FinancialPeriodRepository
) {

    @GetMapping
    fun get(filter: FinancialPeriodFilter, pageable: Pageable): ResponseEntity<Page<FinancialPeriodView>> {
        return financialPeriodRepository.findAll(filter.toSpecification(), pageable)
            .map { financialPeriodMapper.map(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<FinancialPeriodView> {
        return financialPeriodRepository.findByExternalId(id)
            ?.let { financialPeriodMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("financialPeriodId" to id))
    }

    @GetMapping("/active")
    fun getActive(pageable: Pageable): ResponseEntity<Page<FinancialPeriodView>> {
        return financialPeriodRepository.findByStatus(FinancialPeriod.Status.ACTIVE, pageable)
            .map { financialPeriodMapper.map(it) }
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    fun create(@RequestBody @Valid form: FinancialPeriodCreateForm): ResponseEntity<Any> {

        val toCreate = financialPeriodMapper.map(form)
        val created = financialPeriodService.create(toCreate)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid form: FinancialPeriodUpdateForm
    ): ResponseEntity<FinancialPeriodView> {

        val financialPeriod = financialPeriodRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException(mapOf("financialPeriodId" to id))

        financialPeriodMapper.map(form, financialPeriod)
        financialPeriodService.update(financialPeriod)

        return ResponseEntity.ok(financialPeriodMapper.map(financialPeriod))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        financialPeriodRepository.findByExternalId(id)
            ?.let { financialPeriodService.delete(it) }
            ?: throw ResourceNotFoundException(mapOf("financialPeriodId" to id))
        return ResponseEntity.ok().build()
    }
}