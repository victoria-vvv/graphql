package com.project.finance.fetcher.query

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.LoanPaymentMapper
import com.project.finance.model.LoanPayment
import com.project.finance.repository.LoanRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class LoanPaymentsForActiveLoansFetcher(
    private val loanRepository: LoanRepository,
    private val loanPaymentMapper: LoanPaymentMapper
) : ReactorDataFetcher<List<LoanPayment>>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "getLoanPaymentsForActiveLoans"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<List<LoanPayment>> {
        val userId = environment.getArgument<String>("userId")

        return loanRepository.getLoanPaymentsForActiveLoans(userId)
            .map { list -> list.map { loanPaymentMapper.mapToDto(it) } }
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
