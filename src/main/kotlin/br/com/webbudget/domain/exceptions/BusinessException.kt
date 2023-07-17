package br.com.webbudget.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

open class BusinessException(message: String, val detail: String, val httpStatus: HttpStatus = BAD_REQUEST) :
    RuntimeException(message)
