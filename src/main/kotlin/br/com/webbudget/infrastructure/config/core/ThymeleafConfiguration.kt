package br.com.webbudget.infrastructure.config.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode.HTML
import java.nio.charset.StandardCharsets

@Configuration
class ThymeleafConfiguration {

    private lateinit var templateResolver: SpringResourceTemplateResolver

    @Bean
    fun configureTemplateEngine(): SpringTemplateEngine {

        val templateResolver = configureTemplateResolver()

        val engine = SpringTemplateEngine()
        engine.addTemplateResolver(templateResolver)

        return engine
    }

    @Bean
    fun configureTemplateResolver(): SpringResourceTemplateResolver {

        if (::templateResolver.isInitialized) {
            return templateResolver
        }

        templateResolver = SpringResourceTemplateResolver()

        templateResolver.prefix = "classpath:/mail-templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = HTML
        templateResolver.characterEncoding = StandardCharsets.UTF_8.name()

        return templateResolver
    }
}
