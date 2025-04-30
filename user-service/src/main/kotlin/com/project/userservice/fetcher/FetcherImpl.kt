package com.project.userservice.fetcher

import graphql.schema.DataFetcher

interface FetcherImpl {
    val typeName: String
    val fieldName: String
    fun getDataFetcher(): DataFetcher<*>
    val requiresAuth: Boolean get() = false
    val allowedRoles: Set<String> get() = emptySet()
}