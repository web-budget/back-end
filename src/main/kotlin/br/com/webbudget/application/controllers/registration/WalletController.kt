package br.com.webbudget.application.controllers.registration

import br.com.webbudget.application.mappers.registration.WalletMapper
import br.com.webbudget.application.payloads.registration.WalletCreateForm
import br.com.webbudget.application.payloads.registration.WalletFilter
import br.com.webbudget.application.payloads.registration.WalletUpdateForm
import br.com.webbudget.application.payloads.registration.WalletView
import br.com.webbudget.domain.exceptions.ResourceNotFoundException
import br.com.webbudget.domain.services.registration.WalletService
import br.com.webbudget.infrastructure.repository.registration.WalletRepository
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
@RequestMapping("/api/registration/wallets")
class WalletController(
    private val walletMapper: WalletMapper,
    private val walletService: WalletService,
    private val walletRepository: WalletRepository
) {

    @GetMapping
    fun get(filter: WalletFilter, pageable: Pageable): ResponseEntity<Page<WalletView>> {
        return walletRepository.findAll(filter.toSpecification(), pageable)
            .map { walletMapper.map(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<WalletView> {
        return walletRepository.findByExternalId(id)
            ?.let { walletMapper.map(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: throw ResourceNotFoundException(mapOf("walletId" to id))
    }

    @PostMapping
    fun create(@RequestBody @Valid form: WalletCreateForm): ResponseEntity<Any> {

        val toCreate = walletMapper.map(form)
        val created = walletService.create(toCreate)

        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid form: WalletUpdateForm): ResponseEntity<WalletView> {

        val wallet = walletRepository.findByExternalId(id)
            ?: throw ResourceNotFoundException(mapOf("walletId" to id))

        walletMapper.map(form, wallet)
        walletService.update(wallet)

        return ResponseEntity.ok(walletMapper.map(wallet))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Any> {
        walletRepository.findByExternalId(id)
            ?.let { walletService.delete(it) }
            ?: throw ResourceNotFoundException(mapOf("walletId" to id))
        return ResponseEntity.ok().build()
    }
}
