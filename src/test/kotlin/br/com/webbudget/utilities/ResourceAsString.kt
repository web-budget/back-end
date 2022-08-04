package br.com.webbudget.utilities

import org.junit.jupiter.api.extension.ExtendWith

@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(ResourceAsStringResolver::class)
annotation class ResourceAsString(val value: String)
