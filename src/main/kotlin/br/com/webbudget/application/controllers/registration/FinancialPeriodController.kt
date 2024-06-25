package br.com.webbudget.application.controllers.registration

import br.com.webbudget.domain.services.registration.FinancialPeriodService
import br.com.webbudget.infrastructure.repository.registration.FinancialPeriodRepository
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/registration/financial-periods")
class FinancialPeriodController(
    private val financialPeriodService: FinancialPeriodService,
    private val financialPeriodRepository: FinancialPeriodRepository
) {

    fun get(): ResponseEntity<Page<FinancialPeriodView>> {

    }

    fun getById(): ResponseEntity<FinancialPeriodView> {

    }

    fun create() {

    }

    fun delete() {

    }
}