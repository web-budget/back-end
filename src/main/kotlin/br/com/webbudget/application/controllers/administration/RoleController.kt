package br.com.webbudget.application.controllers.administration

import br.com.webbudget.infrastructure.repository.administration.RoleRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/administration/roles")
class RoleController(
    private val roleRepository: RoleRepository
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<String>> = roleRepository.findAll()
        .map { it.name }
        .toList()
        .let { ResponseEntity.ok(it) }
}
