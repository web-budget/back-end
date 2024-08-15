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
import java.util.UUID

class FinancialPeriodMapperTest {

    private var financialPeriodMapper = FinancialPeriodMapperImpl()

    @Test
    fun `should map create form to domain object`() {

        val form = FinancialPeriodCreateForm(
            "08/2024",
            LocalDate.now(),
            LocalDate.now().plusDays(5),
            BigDecimal.ONE,
            BigDecimal.TWO
        )

        val domainObject = financialPeriodMapper.map(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(form.startingAt)
                assertThat(it.endingAt).isEqualTo(form.endingAt)
                assertThat(it.revenuesGoal).isEqualTo(form.revenuesGoal)
                assertThat(it.expensesGoal).isEqualTo(form.expensesGoal)
                assertThat(it.status).isEqualTo(FinancialPeriod.Status.ACTIVE)
            })
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createFinancialPeriod()
        val form = FinancialPeriodUpdateForm(
            "08/2024",
            LocalDate.now(),
            LocalDate.now().plusDays(5),
            BigDecimal.ONE,
            BigDecimal.TWO
        )

        financialPeriodMapper.map(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.startingAt).isEqualTo(form.startingAt)
                assertThat(it.endingAt).isEqualTo(form.endingAt)
                assertThat(it.revenuesGoal).isEqualTo(form.revenuesGoal)
                assertThat(it.expensesGoal).isEqualTo(form.expensesGoal)
                assertThat(it.status).isEqualTo(FinancialPeriod.Status.ACTIVE)
            })
    }

    @Test
    fun `should map domain object to view`() {

        val externalId = UUID.randomUUID()
        val domainObject = createFinancialPeriod(externalId = externalId)

        val view = financialPeriodMapper.map(domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.externalId).isEqualTo(view.id)
                assertThat(it.name).isEqualTo(view.name)
                assertThat(it.startingAt).isEqualTo(view.startingAt)
                assertThat(it.endingAt).isEqualTo(view.endingAt)
                assertThat(it.revenuesGoal).isEqualTo(view.revenuesGoal)
                assertThat(it.expensesGoal).isEqualTo(view.expensesGoal)
                assertThat(it.status.name).isEqualTo(view.status)
            })
    }
}