package br.com.webbudget.validators.registration

import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.entities.registration.MovementClass
import br.com.webbudget.domain.exceptions.BusinessException
import br.com.webbudget.domain.exceptions.ErrorCodes.BUDGET_LIMIT_EXCEEDED
import br.com.webbudget.domain.projections.registration.BudgetAllocated
import br.com.webbudget.domain.validators.registration.BudgetLimitValidator
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixtures.createCostCenter
import br.com.webbudget.utilities.fixtures.createMovementClass
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
    private lateinit var movementClassRepository: MovementClassRepository

    @InjectMockKs
    private lateinit var budgetLimitValidator: BudgetLimitValidator

    @ParameterizedTest
    @MethodSource("nullBudgetMovementClasses")
    fun `should not perform validation if no budget`(movementClass: MovementClass) {
        assertDoesNotThrow { budgetLimitValidator.validate(movementClass) }
    }

    @ParameterizedTest
    @MethodSource("nonNullBudgetMovementClasses")
    fun `should not perform validation cost center doesn't have budget`(movementClass: MovementClass) {
        assertDoesNotThrow { budgetLimitValidator.validate(movementClass) }
    }

    @ParameterizedTest
    @MethodSource("wrongBudgetValueClasses")
    fun `should thrown exception if no budget is available when creating new`(movementClass: MovementClass) {

        val costCenter = movementClass.costCenter
        val movementClassType = movementClass.type

        val budgetAllocated = mockk<BudgetAllocated>()
        every { budgetAllocated.total } returns BigDecimal.TWO

        every {
            movementClassRepository.findBudgetAllocatedByCostCenter(costCenter, movementClassType)
        } returns budgetAllocated

        assertThatThrownBy { budgetLimitValidator.validate(movementClass) }
            .isInstanceOf(BusinessException::class.java)
            .hasFieldOrPropertyWithValue("key", BUDGET_LIMIT_EXCEEDED)
            .hasMessageContaining("Only [0] of [$movementClassType] budget is available")

        verify(exactly = 1) {
            movementClassRepository.findBudgetAllocatedByCostCenter(ofType<CostCenter>(),ofType<MovementClass.Type>())
        }

        confirmVerified(movementClassRepository)
    }

    @ParameterizedTest
    @MethodSource("wrongBudgetValueClasses")
    fun `should thrown exception if no budget is available for when updating`(movementClass: MovementClass) {

        val currentMovementClass = movementClass.apply {
            this.id = 1L
            this.budget = BigDecimal.TWO
            this.externalId = UUID.randomUUID()
        }

        val savedMovementClass = movementClass.apply {
            this.id = 1L
            this.externalId = UUID.randomUUID()
        }

        val costCenter = savedMovementClass.costCenter
        val movementClassType = savedMovementClass.type

        every {
            movementClassRepository.findByExternalId(savedMovementClass.externalId!!)
        } returns currentMovementClass

        val budgetAllocated = mockk<BudgetAllocated>()
        every { budgetAllocated.total } returns BigDecimal("4")

        every {
            movementClassRepository.findBudgetAllocatedByCostCenter(costCenter, movementClassType)
        } returns budgetAllocated

        assertThatThrownBy { budgetLimitValidator.validate(savedMovementClass) }
            .isInstanceOf(BusinessException::class.java)
            .hasFieldOrPropertyWithValue("key", BUDGET_LIMIT_EXCEEDED)
            .hasMessageContaining("Only [0] of [$movementClassType] budget is available")

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 1) {
            movementClassRepository.findBudgetAllocatedByCostCenter(ofType<CostCenter>(),ofType<MovementClass.Type>())
        }

        confirmVerified(movementClassRepository)
    }

    @ParameterizedTest
    @MethodSource("wrongBudgetValueClasses")
    fun `should thrown exception if movement class isn't found`(movementClass: MovementClass) {

        val savedMovementClass = movementClass.apply {
            id = 1L
            externalId = UUID.randomUUID()
        }

        every {
            movementClassRepository.findByExternalId(savedMovementClass.externalId!!)
        } returns null

        assertThatThrownBy { budgetLimitValidator.validate(savedMovementClass) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("Can't find movement class with external id [${savedMovementClass.externalId}]")

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }
        verify(exactly = 0) {
            movementClassRepository.findBudgetAllocatedByCostCenter(ofType<CostCenter>(),ofType<MovementClass.Type>())
        }

        confirmVerified(movementClassRepository)
    }

    companion object {

        @JvmStatic
        fun nullBudgetMovementClasses(): Stream<Arguments> = Stream.of(
                Arguments.of(createMovementClass(
                    budget = null,
                    costCenter = createCostCenter(expenseBudget = BigDecimal.TWO),
                    type = MovementClass.Type.EXPENSE
                )),
            Arguments.of(createMovementClass(
                    budget = null,
                    costCenter = createCostCenter(incomeBudget = BigDecimal.TWO),
                    type = MovementClass.Type.INCOME
                )),
            )

        @JvmStatic
        fun nonNullBudgetMovementClasses(): Stream<Arguments> = Stream.of(
            Arguments.of(createMovementClass(
                budget = BigDecimal.TWO,
                costCenter = createCostCenter(expenseBudget = null),
                type = MovementClass.Type.EXPENSE
            )),
            Arguments.of(createMovementClass(
                budget = BigDecimal.TWO,
                costCenter = createCostCenter(incomeBudget = null),
                type = MovementClass.Type.INCOME
            )),
        )

        @JvmStatic
        fun wrongBudgetValueClasses(): Stream<Arguments> = Stream.of(
            Arguments.of(createMovementClass(
                budget = BigDecimal.ONE,
                costCenter = createCostCenter(expenseBudget = BigDecimal.TWO),
                type = MovementClass.Type.EXPENSE
            )),
            Arguments.of(createMovementClass(
                budget = BigDecimal.ONE,
                costCenter = createCostCenter(incomeBudget = BigDecimal.TWO),
                type = MovementClass.Type.INCOME
            )),
        )
    }
}