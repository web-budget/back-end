package br.com.webbudget

import br.com.webbudget.infrastructure.config.spring.SecurityConfiguration
import br.com.webbudget.utilities.ResourceAsStringResolver
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@Import(SecurityConfiguration::class)
@Suppress("UnnecessaryAbstractClass")
@ExtendWith(MockKExtension::class, ResourceAsStringResolver::class)
abstract class BaseControllerIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc
}
