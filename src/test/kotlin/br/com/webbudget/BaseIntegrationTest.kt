package br.com.webbudget

import br.com.webbudget.utilities.TestContainersInitializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("test")
@ContextConfiguration(initializers = [TestContainersInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest internal constructor() {

    companion object {
        const val OBJECT_NOT_FOUND_ERROR = "Invalid state, object not found"
    }
}
