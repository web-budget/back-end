package br.com.webbudget.mappers.financial

import br.com.webbudget.application.mappers.financial.ApportionmentMapperImpl
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.application.payloads.financial.ApportionmentForm
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.fixtures.createApportionment
import br.com.webbudget.utilities.fixtures.createMovementClass
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ApportionmentMapperUTest {

    @MockK
    private lateinit var movementClassRepository: MovementClassRepository

    private val apportionmentMapper = ApportionmentMapperImpl()

    @BeforeEach
    fun setup() {
        val movementClassMapper = MovementClassMapperImpl()
        ReflectionTestUtils.setField(movementClassMapper, "costCenterMapper", CostCenterMapperImpl())

        ReflectionTestUtils.setField(apportionmentMapper, "movementClassMapper", movementClassMapper)
        ReflectionTestUtils.setField(apportionmentMapper, "movementClassRepository", movementClassRepository)
    }

    @Test
    fun `should map create form to domain object`() {

        val movementClassExternalId = UUID.randomUUID()
        val movementClass = createMovementClass(externalId = movementClassExternalId)

        val form = ApportionmentForm(BigDecimal.ONE, movementClassExternalId)

        every { movementClassRepository.findByExternalId(movementClassExternalId) } returns movementClass

        val apportionment = apportionmentMapper.mapToDomain(form)

        assertThat(apportionment)
            .isNotNull
            .satisfies({
                assertThat(it.value).isEqualTo(form.value)
                assertThat(it.movementClass.externalId).isEqualTo(movementClass.externalId)
            })

        verify(exactly = 1) { movementClassRepository.findByExternalId(ofType<UUID>()) }

        confirmVerified(movementClassRepository)
    }

    @Test
    fun `should map domain object to view`() {

        val apportionment = createApportionment()

        val view = apportionmentMapper.mapToView(apportionment)

        assertThat(view)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(apportionment.externalId)
                assertThat(it.value).isEqualTo(apportionment.value)
            })

        assertThat(view.movementClass)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(apportionment.movementClass.externalId)
                assertThat(it.name).isEqualTo(apportionment.movementClass.name)
                assertThat(it.type).isEqualTo(apportionment.movementClass.type.name)
                assertThat(it.active).isEqualTo(apportionment.movementClass.active)
            })

        assertThat(view.movementClass.costCenter)
            .isNotNull
            .satisfies({
                assertThat(it.id).isEqualTo(apportionment.movementClass.costCenter.externalId)
                assertThat(it.name).isEqualTo(apportionment.movementClass.costCenter.name)
                assertThat(it.active).isEqualTo(apportionment.movementClass.costCenter.active)
            })
    }
}