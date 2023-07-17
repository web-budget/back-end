package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus.NOT_FOUND

class ResourceNotFoundException(filter: Map<String, *>) :
    BusinessException("Can't find resource with the filters provided", filter.toString(), NOT_FOUND)
