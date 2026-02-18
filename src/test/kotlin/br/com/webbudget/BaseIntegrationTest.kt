package br.com.webbudget

import br.com.webbudget.configuration.TestcontainersConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(value = [TestcontainersConfiguration::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest internal constructor() {

    companion object {
        const val OBJECT_NOT_FOUND_ERROR = "Invalid state, object not found"
    }
}
