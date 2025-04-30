package com.project.finance.fetcher.mutation

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.LoanPaymentMapper
import com.project.finance.model.LoanPayment
import com.project.finance.repository.LoanPaymentRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@Component
class LoanPaymentCreateFetcher(private val loanPaymentRepository: LoanPaymentRepository,
    private val loanPaymentMapper: LoanPaymentMapper
): ReactorDataFetcher<LoanPayment>, FetcherImpl {
    override val typeName = "Mutation"
    override val fieldName = "makeLoanPayment"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<LoanPayment> {
        val loanId = environment.getArgument<String>("loanId")
        val amount = environment.getArgument<BigDecimal>("amount")

        return loanPaymentRepository.createLoanPayment(loanId, amount)
            .map { loanPaymentMapper.mapToDto(it) }
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
