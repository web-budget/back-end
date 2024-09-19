package br.com.webbudget.domain.exceptions

open class ConflictingPropertyException(
    val conflicts: Map<String, Any?>,
    override val message: String = DEFAULT_MESSAGE
) : RuntimeException(message) {

    companion object {
        const val DEFAULT_MESSAGE = "Some properties are using the same value than other resources"
    }
}
