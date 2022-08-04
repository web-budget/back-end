package br.com.webbudget.utilities

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.springframework.test.context.junit.jupiter.SpringExtension

class ResourceAsStringResolver : ParameterResolver {

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {

        val isAnnotated = parameterContext.isAnnotated(ResourceAsString::class.java)
        val isStringType = parameterContext.parameter.type.equals(String::class.java)

        return isAnnotated && isStringType
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {

        val applicationContext = SpringExtension.getApplicationContext(extensionContext)

        val value = parameterContext.findAnnotation(ResourceAsString::class.java)
            .map { it.value }
            .orElseThrow { IllegalStateException("Invalid test resource, value not found") }

        val resource = applicationContext.getResource("classpath:/payloads/$value")

        return Resources.toString(resource.url, Charsets.UTF_8)
    }
}
