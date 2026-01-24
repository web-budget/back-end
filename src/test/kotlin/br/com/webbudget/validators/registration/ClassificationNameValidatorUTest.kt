package br.com.webbudget.validators.registration

import br.com.webbudget.domain.exceptions.ConflictingPropertyException
import br.com.webbudget.domain.validators.registration.ClassificationNameValidator
import br.com.webbudget.infrastructure.repository.registration.ClassificationRepository
import br.com.webbudget.utilities.fixtures.createClassification
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.AssertionsForClassTypes.assertThatNoException
import org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ClassificationNameValidatorUTest {

    @MockK
    private lateinit var classificationRepository: ClassificationRepository

    @InjectMockKs
    private lateinit var classificationNameValidator: ClassificationNameValidator

    @Test
    fun `should fail for different entities and equal name`() {

        every { classificationRepository.findByNameIgnoreCase(eq("Classification")) } returns createClassification()

        val toValidate = createClassification(id = null, externalId = null)

        assertThatThrownBy { classificationNameValidator.validate(toValidate) }
            .isInstanceOf(ConflictingPropertyException::class.java)

        verify(exactly = 1) { classificationRepository.findByNameIgnoreCase(eq("Classification")) }

        confirmVerified(classificationRepository)
    }

    @Test
    fun `should not fail if entities are equal`() {

        val externalId = UUID.randomUUID()
        val toValidate = createClassification(id = 1L, externalId = externalId)

        every {
            classificationRepository.findByNameIgnoreCaseAndExternalIdNot(eq("Classification"), externalId)
        } returns null

        assertThatNoException()
            .isThrownBy { classificationNameValidator.validate(toValidate) }

        verify(exactly = 1) {
            classificationRepository.findByNameIgnoreCaseAndExternalIdNot(eq("Classification"), externalId)
        }

        confirmVerified(classificationRepository)
    }
}