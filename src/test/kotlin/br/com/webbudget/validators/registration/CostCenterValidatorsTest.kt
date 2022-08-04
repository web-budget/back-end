package br.com.webbudget.validators.registration

import br.com.webbudget.BaseIntegrationTest
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.domain.exceptions.DuplicatedPropertyException
import br.com.webbudget.domain.validators.registration.DuplicatedNameValidator
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CostCenterValidatorsTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @Autowired
    private lateinit var duplicatedNameValidator: DuplicatedNameValidator

    @BeforeEach
    fun clearDatabase() {
        costCenterRepository.deleteAll()
    }

    @Test
    fun `should fail for different entities and equal name`() {

        costCenterRepository.save(CostCenter("Duplicated", true))

        val duplicated = CostCenter("Duplicated", true)

        assertThatThrownBy { duplicatedNameValidator.validate(duplicated) }
            .isInstanceOf(DuplicatedPropertyException::class.java)
            .hasMessage("cost-center.name")
    }

    @Test
    fun `should not fail if entities are equal`() {

        val saved = CostCenter("Not duplicated", true)
        costCenterRepository.save(saved)

        assertThatNoException()
            .isThrownBy { duplicatedNameValidator.validate(saved) }
    }
}
