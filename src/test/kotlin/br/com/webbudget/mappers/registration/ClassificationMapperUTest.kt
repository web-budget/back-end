package br.com.webbudget.mappers.registration

import br.com.webbudget.application.mappers.registration.ClassificationMapper
import br.com.webbudget.application.payloads.registration.ClassificationCreateForm
import br.com.webbudget.application.payloads.registration.ClassificationUpdateForm
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.entities.registration.Classification.Type.EXPENSE
import br.com.webbudget.domain.entities.registration.Classification.Type.INCOME
import br.com.webbudget.utilities.fixtures.createClassification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal

class ClassificationMapperUTest {

    private lateinit var classificationMapper: ClassificationMapper

    @BeforeEach
    fun setup() {
        classificationMapper = ClassificationMapper()
    }

    @ParameterizedTest
    @MethodSource("createFormObjects")
    fun `should map create form to domain object`(form: ClassificationCreateForm) {

        val domainObject = classificationMapper.mapToDomain(form)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.type).isEqualTo(form.type)
                assertThat(it.budget).isEqualTo(form.budget)
                assertThat(it.description).isEqualTo(form.description)
            })
    }

    @Test
    fun `should map update form to domain object`() {

        val domainObject = createClassification()
        val form = ClassificationUpdateForm("Expenses", BigDecimal.TEN, "Description", false)

        classificationMapper.mapToDomain(form, domainObject)

        assertThat(domainObject)
            .isNotNull
            .satisfies({
                assertThat(it.name).isEqualTo(form.name)
                assertThat(it.budget).isEqualTo(form.budget)
                assertThat(it.description).isEqualTo(form.description)
                assertThat(it.active).isEqualTo(form.active)
            })
    }

    @ParameterizedTest
    @MethodSource("domainObjects")
    fun `should map domain object to view`(domainObject: Classification) {

        domainObject.apply {
            this.id = 1L
            this.externalId = java.util.UUID.randomUUID()
        }

        val view = classificationMapper.mapToView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId!!)
                assertThat(it.name).isEqualTo(view.name)
                assertThat(it.type).isEqualTo(view.type)
                assertThat(it.budget).isEqualTo(view.budget)
                assertThat(it.description).isEqualTo(view.description)
                assertThat(it.active).isTrue()
            })
    }

    @ParameterizedTest
    @MethodSource("domainObjects")
    fun `should map domain object to list view`(domainObject: Classification) {

        domainObject.apply {
            this.id = 1L
            this.externalId = java.util.UUID.randomUUID()
        }

        val view = classificationMapper.mapToListView(domainObject)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(domainObject.externalId!!)
                assertThat(it.name).isEqualTo(view.name)
                assertThat(it.type).isEqualTo(view.type)
                assertThat(it.active).isTrue()
            })
    }

    companion object {

        @JvmStatic
        fun createFormObjects() = listOf(
            Arguments.of(ClassificationCreateForm("Income", INCOME, BigDecimal.ONE, "Description")),
            Arguments.of(ClassificationCreateForm("Expense", EXPENSE, BigDecimal.ONE, "Description"))
        )

        @JvmStatic
        fun domainObjects() = listOf(
            Arguments.of(createClassification(name = "Expenses", type = EXPENSE)),
            Arguments.of(createClassification(name = "Incomes", type = INCOME))
        )
    }
}
