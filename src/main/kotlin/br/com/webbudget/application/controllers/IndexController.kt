package br.com.webbudget.application.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class IndexController {

    @GetMapping("/")
    fun index(): RedirectView {
        return RedirectView("/swagger/openapi/swagger-ui.html");
    }
}