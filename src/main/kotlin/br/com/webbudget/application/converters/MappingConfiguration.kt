package br.com.webbudget.application.converters

import org.mapstruct.MapperConfig

@MapperConfig(componentModel = "spring", uses = [org.mapstruct.extensions.spring.converter.ConversionServiceAdapter::class])
interface MappingConfiguration
