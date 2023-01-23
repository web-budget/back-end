package br.com.webbudget

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
abstract class BaseControllerIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc
}
