package br.com.webbudget.controllers

import br.com.webbudget.BaseControllerIntegrationTest
import br.com.webbudget.application.controllers.UserAccountController
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(UserAccountController::class)
class UserAccountControllerTest : BaseControllerIntegrationTest() {

    // TODO create all controller testes here!

    override fun enableAuthorizationTest() = false

    override fun getEndpointUrl() = ENDPOINT_URL

    companion object {
        private const val ENDPOINT_URL = "/user-account"
    }
}
