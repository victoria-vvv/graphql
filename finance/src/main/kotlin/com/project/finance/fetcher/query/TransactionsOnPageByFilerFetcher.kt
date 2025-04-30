package com.project.finance.fetcher.query

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.TransactionMapper
import com.project.finance.model.Transaction
import com.project.finance.repository.TransactionRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

@Component
class TransactionsOnPageByFilerFetcher(private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper):
ReactorDataFetcher<List<Transaction>>, FetcherImpl
{
    override val typeName = "Query"
    override val fieldName = "getTransactionsOnPageByFilter"

    override fun getDataFetcher(): DataFetcher<*> = this

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<List<Transaction>> {
        val accountId = environment.getArgument<String>("accountId")
        val page = environment.getArgument<Int>("page")
        val size = environment.getArgument<Int>("size")
        val createDate = environment.getArgument<LocalDate>("createdDate")
        val category = environment.getArgument<String>("category")

        return transactionRepository.getTransactionsOnPageByFilter(accountId, page, size, createDate, category)
            .map { list -> list.map { transactionMapper.mapToDto(it) } }
            .toFuture()
    }
}