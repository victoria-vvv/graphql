package com.project.finance.fetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.reactivestreams.Publisher

fun interface ReactorSubscriptionFetcher<T> : DataFetcher<Publisher<T>> {
    fun stream(env: DataFetchingEnvironment): Publisher<T>

    override fun get(environment: DataFetchingEnvironment): Publisher<T> = stream(environment)
}