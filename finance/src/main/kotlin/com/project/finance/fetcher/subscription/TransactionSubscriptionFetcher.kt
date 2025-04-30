package com.project.finance.fetcher.subscription

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorSubscriptionFetcher
import com.project.finance.model.Transaction
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks

@Component
class TransactionSubscriptionFetcher(
    private val transactionSinks: Sinks.Many<Transaction>
) : ReactorSubscriptionFetcher<Transaction>, FetcherImpl {

    override val typeName = "Subscription"
    override val fieldName = "subscribeToTransactionCreated"

    override fun stream(env: DataFetchingEnvironment): Publisher<Transaction> {
        log.info("SUBSCRIPTION: Listening for new transactions")
        return transactionSinks.asFlux()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
