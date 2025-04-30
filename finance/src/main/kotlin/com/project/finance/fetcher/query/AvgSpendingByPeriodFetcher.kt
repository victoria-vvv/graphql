package com.project.finance.fetcher.query

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.repository.TransactionRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@Component
class AvgSpendingByPeriodFetcher(private val transactionRepository: TransactionRepository):
    ReactorDataFetcher<BigDecimal>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "getAverageSpendingByPeriod"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<BigDecimal> {
        val period = environment.getArgument<String>("period")
        val transactionType = environment.getArgument<String?>("transactionType") ?: "debit"

        return Mono.from(transactionRepository.getAverageSpendingByPeriod(period, transactionType))
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}

