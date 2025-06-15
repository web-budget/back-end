package br.com.webbudget.domain.projections.registration

import java.math.BigDecimal

interface BudgetAllocated {
    val total: BigDecimal
}