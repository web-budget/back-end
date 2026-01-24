package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.Classification
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ErrorCodes.BUDGET_LIMIT_EXCEEDED
import br.com.webbudget.domain.projections.registration.BudgetAllocated
import br.com.webbudget.domain.validators.registration.BudgetLimitValidator
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.utilities.fixtures.createCostCenter
import br.com.webbudget.utilities.fixtures.createClassification
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.UUID
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
class BudgetLimitValidatorUTest {

    @MockK
    private lateinit var classificationRepository: ClassificationRepository

    @InjectMockKs
    private lateinit var budgetLimitValidator: BudgetLimitValidator

    @ParameterizedTest
    @MethodSource("nullBudgetMovementClasses")
    fun `should not perform validation if no budget`(classification: Classification) {
        assertDoesNotThrow { budgetLimitValidator.validate(classification) }
    }

    @ParameterizedTest
    @MethodSource("nonNullBudgetMovementClasses")
    fun `should not perform validation cost center doesn't have budget`(classification: Classification) {
        assertDoesNotThrow { budgetLimitValidator.validate(classification) }
    }

    @ParameterizedTest
    @MethodSource("wrongBudgetValueClasses")
    fun `should thrown exception if no budget is available when creating new`(classification: Classification) {

        val costCenter = classification.costCenter
        val movementClassType = classification.type

        val budgetAllocated = mockk<BudgetAllocated>()
        every { budgetAllocated.total } returns BigDecimal.TWO

        every {
            classificationRepository.findBudgetAllocatedByCostCenter(costCenter, movementClassType)
        } returns budgetAllocated

        assertThatThrownBy { budgetLimitValidator.validate(classification) }
            .isInstanceOf(BusinessException::class.java)
            .hasFieldOrPropertyWithValue("key", BUDGET_LIMIT_EXCEEDED)
            .hasMessageContaining("Only [0] of [$movementClassType] budget is available")

        verify(exactly = 1) {
            classificationRepository.findBudgetAllocatedByCostCenter(ofType<CostCenter>(),ofType<Classification.Type>())
        }

        confirmVerified(classificationRepository)
    }

    @ParameterizedTest
    @MethodSource("wrongBudgetValueClasses")
    fun `should thrown exception if no budget is available for when updating`(classification: Classification) {

        val currentMovementClass = classification.apply {
            this.id = 1L
            this.budget = BigDecimal.TWO
            this.externalId = UUID.randomUUID()
        }

        val savedMovementClass = classification.apply {
            this.id = 1L
            this.externalId = UUID.randomUUID()
        }

        val costCenter = savedMovementClass.costCenter
        val movementClassType = savedMovementClass.type

        every {
            classificationRepository.findByExternalId(savedMovementClass.externalId!!)
        } returns currentMovementClass

        val budgetAllocated = mockk<BudgetAllocated>()
        every { budgetAllocated.total } returns BigDecimal("4")

        every {
            classificationRepository.findBudgetAllocatedByCostCenter(costCenter, movementClassType)
        } returns budgetAllocated

        assertThatThrownBy { budgetLimitValidator.validate(savedMovementClass) }
            .isInstanceOf(BusinessException::class.java)
            .hasFieldOrPropertyWithValue("key", BUDGET_LIMIT_EXCEEDED)
            .hasMessageContaining("Only [0] of [$movementClassType] budget is available")

        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) {
            classificationRepository.findBudgetAllocatedByCostCenter(ofType<CostCenter>(),ofType<Classification.Type>())
        }

        confirmVerified(classificationRepository)
    }

    @ParameterizedTest
    @MethodSource("wrongBudgetValueClasses")
    fun `should thrown exception if movement class isn't found`(classification: Classification) {

        val savedMovementClass = classification.apply {
            id = 1L
            externalId = UUID.randomUUID()
        }

        every {
            classificationRepository.findByExternalId(savedMovementClass.externalId!!)
        } returns null

        assertThatThrownBy { budgetLimitValidator.validate(savedMovementClass) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Can't find movement class with external id [${savedMovementClass.externalId}]")

        verify(exactly = 1) { classificationRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 0) {
            classificationRepository.findBudgetAllocatedByCostCenter(ofType<CostCenter>(),ofType<Classification.Type>())
        }

        confirmVerified(classificationRepository)
    }

    companion object {

        @JvmStatic
        fun nullBudgetMovementClasses(): Stream<Arguments> = Stream.of(
                Arguments.of(createClassification(
                    budget = null,
                    costCenter = createCostCenter(expenseBudget = BigDecimal.TWO),
                    type = Classification.Type.EXPENSE
                )),
            Arguments.of(createClassification(
                    budget = null,
                    costCenter = createCostCenter(incomeBudget = BigDecimal.TWO),
                    type = Classification.Type.INCOME
                )),
            )

        @JvmStatic
        fun nonNullBudgetMovementClasses(): Stream<Arguments> = Stream.of(
            Arguments.of(createClassification(
                budget = BigDecimal.TWO,
                costCenter = createCostCenter(expenseBudget = null),
                type = Classification.Type.EXPENSE
            )),
            Arguments.of(createClassification(
                budget = BigDecimal.TWO,
                costCenter = createCostCenter(incomeBudget = null),
                type = Classification.Type.INCOME
            )),
        )

        @JvmStatic
        fun wrongBudgetValueClasses(): Stream<Arguments> = Stream.of(
            Arguments.of(createClassification(
                budget = BigDecimal.ONE,
                costCenter = createCostCenter(expenseBudget = BigDecimal.TWO),
                type = Classification.Type.EXPENSE
            )),
            Arguments.of(createClassification(
                budget = BigDecimal.ONE,
                costCenter = createCostCenter(incomeBudget = BigDecimal.TWO),
                type = Classification.Type.INCOME
            )),
        )
    }
}