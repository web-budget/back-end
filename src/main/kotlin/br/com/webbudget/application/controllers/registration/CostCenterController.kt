package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.mappers.registration.CostCenterMapper
import br.com.webbudget.application.payloads.registration.CostCenterFilter
import br.com.webbudget.application.payloads.registration.CostCenterForm
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.registration.CostCenterService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
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
@RequestMapping("/api/registration/cost-centers")
class CostCenterController(
    private val costCenterMapper: CostCenterMapper,
    private val costCenterService: CostCenterService,
    private val costCenterRepository: CostCenterRepository
) {

    @GetMapping
    fun get(filter: CostCenterFilter, pageable: Pageable): ResponseEntity<Page<CostCenterView>> {
        return costCenterRepository.findAll(filter.toSpecification(), pageable)
            .map { costCenterMapper.map(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CostCenterView> {
        return costCenterRepository.findByExternalId(id)
            ?.let { costCenterMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("id" to id))
    }

    @PostMapping
    fun create(@RequestBody @Valid form: CostCenterForm): ResponseEntity<Any> {

        val toCreate = costCenterMapper.map(form)
        val created = costCenterService.create(toCreate)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid form: CostCenterForm): ResponseEntity<CostCenterView> {
        return costCenterRepository.findByExternalId(id)
            ?.updateFields(form)
            ?.let { costCenterService.update(it) }
            ?.let { ResponseEntity.ok(costCenterMapper.map(it)) }
            ?: throw ResourceNotFoundException(mapOf("id" to id))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        costCenterRepository.findByExternalId(id)
            ?.let { costCenterService.delete(it) }
            ?: throw ResourceNotFoundException(mapOf("id" to id))
        return ResponseEntity.ok().build()
    }
}
