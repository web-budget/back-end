package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseBody
@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(id: UUID) : RuntimeException("Can't find resource with id $id")
