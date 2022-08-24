package br.com.webbudget.infrastructure.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {

    @Bean
    fun springShopOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("WebBudget")
                    .description("O webBudget é um sistema grátis e de código aberto para controle financeiro pessoal ou de pequenas empresas.")
                    .version("v4.0.0")
                    .license(License().name("Apache License 2.0").url("https://github.com/web-budget/back-end/blob/main/LICENSE"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Documentation")
                    .url("https://github.com/web-budget/back-end")
            )
    }
}