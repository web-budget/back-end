package br.com.webbudget.infrastructure.config.spring

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT
import com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
import com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.KotlinFeature.NullIsSameAsDefault
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class JacksonConfiguration {

    @Bean
    fun configureObjectMapper(): Jackson2ObjectMapperBuilderCustomizer {

        val kotlinModule = KotlinModule.Builder()
            .configure(NullIsSameAsDefault, true)
            .build()

        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder
                .modulesToInstall(kotlinModule)
                .deserializers(EnhancedStringDeserializer())
                .failOnUnknownProperties(false)
                .serializationInclusion(NON_NULL)
                .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(
                    ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                    READ_UNKNOWN_ENUM_VALUES_AS_NULL
                )
        }
    }

    class EnhancedStringDeserializer : StdDeserializer<String>(String::class.java) {
        override fun deserialize(parser: JsonParser?, context: DeserializationContext?): String? {
            return parser?.let { StringUtils.stripToNull(it.valueAsString) }
        }
    }
}
