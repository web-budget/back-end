package br.com.webbudget.domain.exceptions

class ResourceNotFoundException : RuntimeException("Can't find any resource with the filters provided")