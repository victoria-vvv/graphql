package com.project.finance.fetcher.mutation

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.ExchangeRateMapper
import com.project.finance.model.ExchangeRate
import com.project.finance.model.ExchangeRateInput
import com.project.finance.repository.ExchangeRateRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.concurrent.CompletableFuture

val log = LoggerFactory.getLogger("logger")

@Component
class ExchangeRateCreateFetcher(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val exchangeRateMapper: ExchangeRateMapper,
    private val objectMapper: ObjectMapper,
    private val exchangeRateSink: Sinks.Many<ExchangeRate>
) : ReactorDataFetcher<ExchangeRate>, FetcherImpl {
    override val typeName = "Mutation"
    override val fieldName = "createExchangeRate"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<ExchangeRate> {
        val exchangeRateInput = environment.getArgument<Map<String, Any>>("input")

        return Mono.fromCallable {
            objectMapper.convertValue(exchangeRateInput, ExchangeRateInput::class.java)
        }
            .flatMap { input -> exchangeRateRepository.createExchangeRate(input) }
            .map { inputRecord -> exchangeRateMapper.mapToDto(inputRecord) }
            .doOnNext { createdExchangeRate ->
                sendToSink(createdExchangeRate)
            }
            .doOnNext{
                l -> log.info("CREATE FETCHER FETCHER: {}", l)
            }
            .toFuture()
    }

    private fun sendToSink(exchangeRate: ExchangeRate) {
        val result = exchangeRateSink.tryEmitNext(exchangeRate)

        if (result == Sinks.EmitResult.OK) {
            log.info("Успешно отправлено в sink: {}", exchangeRate)
        } else {
            log.error("Ошибка при отправке данных в sink: {}. Объект: {}", result, exchangeRate)
        }
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
