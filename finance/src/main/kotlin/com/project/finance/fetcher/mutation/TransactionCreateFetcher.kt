package com.project.finance.fetcher.mutation

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.TransactionMapper
import com.project.finance.model.Transaction
import com.project.finance.model.TransactionInput
import com.project.finance.repository.TransactionRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Component
class TransactionCreateFetcher(
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val objectMapper: ObjectMapper,
    private val sink : Sinks.Many<Transaction>,
) : ReactorDataFetcher<Transaction>, FetcherImpl {
    override val typeName = "Mutation"
    override val fieldName = "createTransaction"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<Transaction> {
        val transactionInput = environment.getArgument<Map<String, Any>>("input")

        return Mono.fromCallable {
            objectMapper.convertValue(transactionInput, TransactionInput::class.java)
        }
            .flatMap {input -> transactionRepository.createTransaction(input) }
            .map {inputRecord -> transactionMapper.mapToDto(inputRecord) }
            .doOnNext{transaction -> sendToSink(transaction)}
            .toFuture()
    }

    fun sendToSink(transaction: Transaction){
        sink.tryEmitNext(transaction)
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}