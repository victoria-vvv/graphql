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
class TransactionsInPeriodByCategoriesFetcher(
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper
) : ReactorDataFetcher<List<Transaction>>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "getTransactionsInPeriodByCategories"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<List<Transaction>> {
        val startDate = environment.getArgument<LocalDate>("startDate")
        val endDate = environment.getArgument<LocalDate>("endDate")
        val category = environment.getArgument<String>("category")

        return transactionRepository.getTransactionsInPeriodByCategories(startDate, endDate, category)
            .map { list -> list.map { transactionMapper.mapToDto(it) } }
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
