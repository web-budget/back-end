package br.com.webbudget.application.payloads.validation

import jakarta.validation.groups.Default
import org.springframework.validation.annotation.Validated

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Validated(value = [OnCreate::class, Default::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class OnCreateValidation
