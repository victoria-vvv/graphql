package com.project.finance.fetcher.query

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.ExchangeRateMapper
import com.project.finance.model.ExchangeRate
import com.project.finance.repository.ExchangeRateRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.concurrent.CompletableFuture
import kotlin.collections.List

@Component
class ExchangeRateGetFetcher(private val exchangeRateRepository: ExchangeRateRepository,
                             private val exchangeRateMapper: ExchangeRateMapper)
    : ReactorDataFetcher<List<ExchangeRate>>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "getHistoricalExchangeRates"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<List<ExchangeRate>>{
        val baseCurrency = environment.getArgument<String>("baseCurrency")
        val targetCurrency = environment.getArgument<String>("targetCurrency")
        val startDate = environment.getArgument<LocalDate>("startDate")
        val endDate = environment.getArgument<LocalDate>("endDate")

        return exchangeRateRepository.getExchangeRates(baseCurrency, targetCurrency, startDate, endDate)
            .map { list -> list.map { exchangeRateMapper.mapToDto(it) } }
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}