package com.project.finance.mapper

interface CommonMapper<in R, out D> {
    fun mapToDto(record: R): D
    fun supports(typeName: String): Boolean
}