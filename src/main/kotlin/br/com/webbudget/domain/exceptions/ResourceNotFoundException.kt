package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseBody
@ResponseStatus(HttpStatus.NO_CONTENT)
class ResourceNotFoundException(message: String) : RuntimeException(message)
