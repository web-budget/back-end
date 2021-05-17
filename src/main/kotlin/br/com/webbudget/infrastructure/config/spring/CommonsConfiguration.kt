package br.com.webbudget.infrastructure.config.spring

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.format.Formatter
import org.springframework.format.FormatterRegistry
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableAsync
@EnableScheduling
@EnableJpaAuditing
class CommonsConfiguration(
    private val formatters: List<Formatter<*>>,
    private val converters: List<Converter<*, *>>
) : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        formatters.forEach(registry::addFormatter)
        converters.forEach(registry::addConverter)
    }

    @Bean
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        return Jackson2ObjectMapperBuilder()
            .modules(KotlinModule())
    }
}
