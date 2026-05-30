package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus

class ResourceNotFoundException : ApplicationException(
    code = "resource-not-found",
    status = HttpStatus.NOT_FOUND,
    message = "Can't find any resource with the filters provided"
)
