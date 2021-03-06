package br.com.webbudget.application.payloads.validation

import org.springframework.validation.annotation.Validated
import javax.validation.groups.Default

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Validated(value = [OnCreate::class, Default::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class OnCreateValidation
