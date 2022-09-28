package br.com.webbudget.controllers.registration

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.payloads.registration.CostCenterView
import br.com.webbudget.domain.entities.registration.CostCenter
import br.com.webbudget.infrastructure.repository.registration.CostCenterRepository
import br.com.webbudget.utilities.ResourceAsString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.util.LinkedMultiValueMap
import java.util.UUID

class CostCenterControllerTest : BaseControllerIntegrationTest() {

    @Autowired
    private lateinit var costCenterRepository: CostCenterRepository

    @BeforeEach
    fun clearDatabase() {
        costCenterRepository.deleteAll()
    }

    @Test
    @Disabled // FIXME when auth works, enable it
    fun `should require authentication`() {
        mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `should create and return created`(@ResourceAsString("cost-center/create.json") payload: String) {

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isCreated() }
        }

        val found = costCenterRepository.findByNameIgnoreCase("Alimentação")

        assertThat(found)
            .isNotNull
            .hasFieldOrProperty("id").isNotNull
            .hasFieldOrProperty("externalId").isNotNull
            .hasFieldOrPropertyWithValue("active", true)
            .hasFieldOrPropertyWithValue("name", "Alimentação")
    }

    @Test
    fun `should update and return success`(@ResourceAsString("cost-center/update.json") payload: String) {

        val created = costCenterRepository.save(CostCenter("To update", true))

        mockMvc.put("$ENDPOINT_URL/${created.externalId}") {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isOk() }
        }

        val found = costCenterRepository.findByExternalId(created.externalId!!)

        assertThat(found)
            .isNotNull
            .hasFieldOrPropertyWithValue("active", false)
            .hasFieldOrPropertyWithValue("name", "Carro")
    }

    @Test
    fun `should delete and return success`() {

        val created = costCenterRepository.save(CostCenter("To delete", true))

        mockMvc.delete("$ENDPOINT_URL/${created.externalId}") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        val found = costCenterRepository.findByExternalId(created.externalId!!)
        assertThat(found).isNull()
    }

    @Test
    fun `should return no content if delete unknown entity`() {

        val randomUuid = UUID.randomUUID()

        mockMvc.delete("$ENDPOINT_URL/$randomUuid") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `should return unprocessable entity if required fields are not present`(
        @ResourceAsString("cost-center/invalid.json") payload: String
    ) {
        val requiredFields = arrayOf("name")

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isUnprocessableEntity() }
        }.andExpect {
            jsonPath("\$.violations[*].property", Matchers.containsInAnyOrder(*requiredFields))
        }
    }

    @Test
    fun `should return conflict if name is duplicated`(@ResourceAsString("cost-center/create.json") payload: String) {

        costCenterRepository.save(CostCenter("Alimentação", true))

        mockMvc.post(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = payload
        }.andExpect {
            status { isConflict() }
        }
    }

    @Test
    fun `should find by external id`() {

        val created = costCenterRepository.save(CostCenter("To find", true))

        val result = mockMvc.get("$ENDPOINT_URL/${created.externalId}") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val found = jsonToObject(result.response.contentAsString, CostCenterView::class.java)

        assertThat(found)
            .isNotNull
            .hasFieldOrPropertyWithValue("name", "To find")
            .hasFieldOrPropertyWithValue("active", true)
    }

    @Test
    fun `should return empty if not found by external id`() {

        val randomUuid = UUID.randomUUID()

        mockMvc.get("$ENDPOINT_URL/$randomUuid") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `should find all using pagination`() {

        val toCreate = listOf(
            CostCenter("Carro", true),
            CostCenter("Casa", false),
            CostCenter("Filhos", true),
            CostCenter("Alimentação", false),
            CostCenter("Empresa", true),
            CostCenter("Educação", false)
        )

        costCenterRepository.saveAll(toCreate)

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", "0")
        parameters.add("size", "2")

        val result = mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            params = parameters
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("\$.totalElements", `is`(6))
            jsonPath("\$.numberOfElements", `is`(2))
            jsonPath("\$.totalPages", `is`(3))
            jsonPath("\$.size", `is`(2))
            jsonPath("\$.empty", `is`(false))
        }.andReturn()

        val content = jsonToObject(result.response.contentAsString, "/content", CostCenterView::class.java)

        assertThat(content)
            .hasSize(2)
            .extracting("name", "active")
            .containsExactlyInAnyOrder(tuple("Carro", true), tuple("Casa", false))
    }

    @Test
    fun `should find using filters`() {

        val toCreate = listOf(
            CostCenter("Carro", true),
            CostCenter("Casa", true),
            CostCenter("Filhos", true),
        )

        costCenterRepository.saveAll(toCreate)

        val parameters = LinkedMultiValueMap<String, String>()

        parameters.add("page", "0")
        parameters.add("size", "1")

        parameters.add("status", "ACTIVE")
        parameters.add("filter", "Filhos")

        val result = mockMvc.get(ENDPOINT_URL) {
            contentType = MediaType.APPLICATION_JSON
            params = parameters
        }.andExpect {
            status { isOk() }
        }.andExpect {
            jsonPath("\$.totalElements", `is`(1))
            jsonPath("\$.numberOfElements", `is`(1))
            jsonPath("\$.totalPages", `is`(1))
            jsonPath("\$.size", `is`(1))
            jsonPath("\$.empty", `is`(false))
        }.andReturn()

        val users = jsonToObject(result.response.contentAsString, "/content", CostCenterView::class.java)

        assertThat(users)
            .hasSize(1)
            .extracting("name", "active")
            .contains(tuple("Filhos", true))
    }

    companion object {
        private const val ENDPOINT_URL = "/api/cost-centers"
    }
}
