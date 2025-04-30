package com.project.finance.fetcher.subscription

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorSubscriptionFetcher
import com.project.finance.model.ExchangeRate
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks

val log = LoggerFactory.getLogger("GraphQLWebSocketHandler")

@Component
class ExchangeRateSubscriptionFetcher(
    private val exchangeRateSink: Sinks.Many<ExchangeRate>
): ReactorSubscriptionFetcher<ExchangeRate>, FetcherImpl {
    override val typeName = "Subscription"
    override val fieldName = "subscribeToCurrencyRateChanges"

    override fun stream(env: DataFetchingEnvironment): Publisher<ExchangeRate> {
        val baseCurrency = env.getArgument<String>("baseCurrency")

        log.info("EXCHANGE RATE SUBSCRIPTION FETCHER")
        return exchangeRateSink.asFlux()
            .filter {it.baseCurrency == baseCurrency}
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}