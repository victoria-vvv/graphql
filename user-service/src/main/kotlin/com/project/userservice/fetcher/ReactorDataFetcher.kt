package com.project.userservice.fetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

fun interface ReactorDataFetcher<T> : DataFetcher<CompletableFuture<T>> {
    fun async(environment: DataFetchingEnvironment): CompletableFuture<T>

    override fun get(environment: DataFetchingEnvironment): CompletableFuture<T> = async(environment)
}
