package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.FinancialPeriodMapperImpl
import br.com.webbudget.application.payloads.registration.FinancialPeriodCreateForm
import br.com.webbudget.application.payloads.registration.FinancialPeriodUpdateForm
import br.com.webbudget.domain.entities.registration.FinancialPeriod
import br.com.webbudget.utilities.fixture.createFinancialPeriod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class FinancialPeriodMapperTest {

    private val financialPeriodMapper = FinancialPeriodMapperImpl()

    @Test
    fun `should map create form to domain object`() {

        val start = LocalDate.of(2024, 1, 1)
        val end = start.plusDays(30)

        val form = FinancialPeriodCreateForm("01/2024", start, end, BigDecimal.TEN, BigDecimal.ONE)

        val financialPeriod = financialPeriodMapper.mapToDomain(form)

        assertThat(financialPeriod)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(start)
                assertThat(it.endingAt).isEqualTo(end)
                assertThat(it.revenuesGoal).isEqualTo(form.revenuesGoal)
                assertThat(it.expensesGoal).isEqualTo(form.expensesGoal)
                assertThat(it.status).isEqualTo(FinancialPeriod.Status.ACTIVE)
            })
    }

    @Test
    fun `should map update form to domain object`() {

        val start = LocalDate.of(2024, 1, 1)
        val end = start.plusDays(30)

        val form = FinancialPeriodUpdateForm("01/2024", start, end, BigDecimal.ZERO, BigDecimal.ZERO)

        val financialPeriod = createFinancialPeriod(id = 1L, status = FinancialPeriod.Status.ENDED)

        financialPeriodMapper.mapToDomain(form, financialPeriod)

        assertThat(financialPeriod)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(start)
                assertThat(it.endingAt).isEqualTo(end)
                assertThat(it.revenuesGoal).isEqualTo(form.revenuesGoal)
                assertThat(it.expensesGoal).isEqualTo(form.expensesGoal)
                assertThat(it.status).isEqualTo(FinancialPeriod.Status.ENDED)
            })
    }

    @Test
    fun `should map domain object to view`() {

        val financialPeriod = createFinancialPeriod(id = 1L, status = FinancialPeriod.Status.ACCOUNTED)

        val view = financialPeriodMapper.mapToView(financialPeriod)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(financialPeriod.externalId)
                assertThat(it.name).isEqualTo(financialPeriod.name)
                assertThat(it.startingAt).isEqualTo(financialPeriod.startingAt)
                assertThat(it.endingAt).isEqualTo(financialPeriod.endingAt)
                assertThat(it.revenuesGoal).isEqualTo(financialPeriod.revenuesGoal)
                assertThat(it.expensesGoal).isEqualTo(financialPeriod.expensesGoal)
                assertThat(it.status).isEqualTo(financialPeriod.status.name)
            })
    }

    @Test
    fun `should map domain object to list view`() {

        val financialPeriod = createFinancialPeriod(id = 1L, status = FinancialPeriod.Status.ACCOUNTED)

        val view = financialPeriodMapper.mapToListView(financialPeriod)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(financialPeriod.externalId)
                assertThat(it.name).isEqualTo(financialPeriod.name)
                assertThat(it.startingAt).isEqualTo(financialPeriod.startingAt)
                assertThat(it.endingAt).isEqualTo(financialPeriod.endingAt)
                assertThat(it.status).isEqualTo(financialPeriod.status.name)
            })
    }
}