package br.com.webbudget.application.mappers

import org.mapstruct.MapperConfig
import org.mapstruct.ReportingPolicy

@MapperConfig(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = [org.mapstruct.extensions.spring.converter.ConversionServiceAdapter::class]
)
interface MappingConfiguration
