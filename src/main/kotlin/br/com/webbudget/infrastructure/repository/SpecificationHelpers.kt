package br.com.webbudget.infrastructure.repository

interface SpecificationHelpers {

    fun likeIgnoreCase(text: String) = "%${text.lowercase()}%"
}
