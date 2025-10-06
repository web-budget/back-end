package br.com.webbudget

import br.com.webbudget.infrastructure.config.core.JacksonConfiguration
import br.com.webbudget.infrastructure.config.security.SecurityConfiguration
import br.com.webbudget.infrastructure.repository.administration.UserRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
@Import(value = [JacksonConfiguration::class, SecurityConfiguration::class])
abstract class BaseControllerIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @MockkBean
    protected lateinit var userRepository: UserRepository
}
