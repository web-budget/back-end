package br.com.webbudget.infrastructure.repository

interface SpecificationHelpers {

    fun likeIgnoringCase(text: String) = "%${text.lowercase()}%"
}
