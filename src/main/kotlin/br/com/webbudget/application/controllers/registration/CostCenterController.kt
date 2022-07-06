package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.payloads.registration.CostCenterFilter
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.springframework.core.convert.ConversionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/cost-centers")
class CostCenterController(
    private val conversionService: ConversionService,
    private val costCenterRepository: CostCenterRepository
) {

    @GetMapping
    fun get(filter: CostCenterFilter, pageable: Pageable): ResponseEntity<Page<CostCenterView>> {
        val response = costCenterRepository.findAll(filter.toSpecification(), pageable)
            .map { conversionService.convert(it, CostCenterView::class.java)!! }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CostCenterView> {
        return costCenterRepository.findByExternalId(id)
            ?.let { conversionService.convert(it, CostCenterView::class.java) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException("Can't find resource with id $id")
    }
}