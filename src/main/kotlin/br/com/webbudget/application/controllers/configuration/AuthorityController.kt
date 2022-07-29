package br.com.webbudget.application.controllers.configuration

import br.com.webbudget.infrastructure.repository.configuration.AuthorityRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/authorities")
class AuthorityController(
    private val authorityRepository: AuthorityRepository
) {

    @GetMapping
    fun get(): ResponseEntity<List<String>> {
        return authorityRepository.findAll()
            .map { it.name }
            .toList()
            .let { ResponseEntity.ok(it) }
    }
}
