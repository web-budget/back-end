package br.com.webbudget.backend.infrastructure.config.spring

import org.springframework.core.convert.converter.Converter
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.format.Formatter
import org.springframework.format.FormatterRegistry
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
}