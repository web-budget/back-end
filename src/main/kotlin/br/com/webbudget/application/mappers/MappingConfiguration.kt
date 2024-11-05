package br.com.webbudget.application.mappers

import org.mapstruct.MapperConfig
import org.mapstruct.MappingConstants.ComponentModel
import org.mapstruct.ReportingPolicy

@MapperConfig(
    componentModel = ComponentModel.SPRING,
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface MappingConfiguration
