package br.com.webbudget.backend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
abstract class AbstractControllerTest : AbstractTest() {

    @Autowired
    protected lateinit var mockMvc: MockMvc
}