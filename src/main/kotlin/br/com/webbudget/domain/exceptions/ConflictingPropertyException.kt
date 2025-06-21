package br.com.webbudget.domain.exceptions

open class ConflictingPropertyException(
    message: String = DEFAULT_MESSAGE,
    key: String = DEFAULT_KEY,
    parameters: Map<String, Any?>? = null
) : BusinessException(message, key, parameters) {

    companion object {
        private const val DEFAULT_KEY = "conflicting-properties"
        private const val DEFAULT_MESSAGE = "Some properties are conflicting with existent data"
    }
}
