package br.com.webbudget

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.core.io.Resource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

@AutoConfigureMockMvc
abstract class AbstractControllerTest : AbstractTest() {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    protected fun objectToJson(payload: Any): String = objectMapper.writeValueAsString(payload)

    protected fun <T> jsonToObject(json: String, valueType: Class<T>): T {
        return objectMapper.readValue(json, valueType)
    }

    protected fun <T> jsonToObject(json: String, node: String, valueType: Class<T>): List<T> {
        val nodes = objectMapper.readTree(json)
        val specificNode = nodes.at(node)
        return objectMapper.readValue(specificNode.toString())
    }

    protected fun resourceAsString(resource: Resource): String {
        return Resources.toString(resource.url, Charsets.UTF_8)
    }

    protected fun fromMap(values: Map<String, String>): MultiValueMap<String, String> {
        val parameters = LinkedMultiValueMap<String, String>()
        values.forEach { (key, value) -> parameters[key] = listOf(value) }
        return parameters
    }
}
