package br.com.webbudget.utilities

import org.junit.jupiter.api.extension.ExtendWith

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@ExtendWith(ResourceAsStringResolver::class)
annotation class ResourceAsString(val value: String)
