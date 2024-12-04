package br.com.webbudget.utilities

import com.google.common.io.Resources
import org.springframework.core.io.ClassPathResource

data class JsonPayload(
    val name: String,
    val location: String = "/payloads/"
) {
    override fun toString(): String = Resources.toString(ClassPathResource("$location$name.json").url, Charsets.UTF_8)
}