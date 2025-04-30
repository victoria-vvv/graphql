package com.project.finance.repository

import reactor.core.publisher.Mono

interface FinanceRepository {
    fun supports(typeName: String): Boolean
    fun getById(id: String): Mono<*>?
}