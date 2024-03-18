package br.com.webbudget.domain.validators

import org.springframework.beans.factory.annotation.Qualifier

@MustBeDocumented
@Qualifier("ON_DELETE")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class OnDeleteValidation