package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.mappers.registration.CardMapper
import br.com.webbudget.application.payloads.registration.CardCreateForm
import br.com.webbudget.application.payloads.registration.CardFilter
import br.com.webbudget.application.payloads.registration.CardUpdateForm
import br.com.webbudget.application.payloads.registration.CardView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.registration.CardService
import br.com.webbudget.infrastructure.repository.registration.CardRepository
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
@RequestMapping("/api/registration/cards")
class CardController(
    private val cardMapper: CardMapper,
    private val cardService: CardService,
    private val cardRepository: CardRepository
) {

    @GetMapping
    fun get(filter: CardFilter, pageable: Pageable): ResponseEntity<Page<CardView>> {
        return cardRepository.findAll(filter.toSpecification(), pageable)
            .map { cardMapper.map(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<CardView> {
        return cardRepository.findByExternalId(id)
            ?.let { cardMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("cardId" to id))
    }

    @PostMapping
    fun create(@RequestBody @Valid form: CardCreateForm): ResponseEntity<Any> {

        val toCreate = cardMapper.map(form)
        val created = cardService.create(toCreate)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid form: CardUpdateForm): ResponseEntity<CardView> {

        val card = cardRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException(mapOf("cardId" to id))

        cardMapper.map(form, card)
        cardService.update(card)

        return ResponseEntity.ok(cardMapper.map(card))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        cardRepository.findByExternalId(id)
            ?.let { cardService.delete(it) }
            ?: throw ResourceNotFoundException(mapOf("cardId" to id))
        return ResponseEntity.ok().build()
    }
}
