package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.registration.MovementClassController
import br.com.webbudget.application.mappers.registration.CostCenterMapperImpl
import br.com.webbudget.application.mappers.registration.MovementClassMapperImpl
import br.com.webbudget.domain.services.registration.MovementClassService
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.infrastructure.repository.registration.MovementClassRepository
import br.com.webbudget.utilities.ResourceAsString
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@WebMvcTest(MovementClassController::class)
@Import(value = [MovementClassMapperImpl::class, CostCenterMapperImpl::class])
class MovementClassControllerTest : BaseControllerIntegrationTest() {

    @MockkBean
    private lateinit var movementClassService: MovementClassService

    @MockkBean
    private lateinit var movementClassRepository: MovementClassRepository

    @MockkBean
    private lateinit var costCenterRepository: CostCenterRepository

    @Test
    fun `should require authorization`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should call create and expect created`(@ResourceAsString("movement-class/create.json") payload: String) {

    }

    @Test
    fun `should call update and expect ok`(@ResourceAsString("wallet/update.json") payload: String) {

    }

    @Test
    fun `should call delete and expect ok`() {

    }

    @Test
    fun `should expect not found if try to delete something that does not exist`() {

    }

    @Test
    fun `should expect unprocessable entity if required fields are not present`(
        @ResourceAsString("movement-class/invalid.json") payload: String
    ) {

    }

    @Test
    fun `should expect conflict if name is duplicated`(
        @ResourceAsString("movement-class/create.json") payload: String
    ) {

    }

    @Test
    fun `should call find by id and expect ok`() {

    }

    @Test
    fun `should call find by id and expect not found if nothing is found`() {

    }

    @Test
    fun `should call get and expect paged result`() {

    }

    companion object {
        private const val ENDPOINT_URL = "/api/registration/movement-classes"
    }
}