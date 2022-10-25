package br.com.webbudget.application.controllers.administration

import br.com.webbudget.infrastructure.repository.administration.AuthorityRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/administration/authorities")
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
