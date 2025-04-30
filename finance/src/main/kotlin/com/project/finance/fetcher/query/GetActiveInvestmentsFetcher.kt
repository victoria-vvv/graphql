package com.project.finance.fetcher.query

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.InvestmentMapper
import com.project.finance.model.Investment
import com.project.finance.repository.InvestmentRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class GetActiveInvestmentsFetcher(private val investmentRepository: InvestmentRepository,
    private val investmentMapper: InvestmentMapper
): ReactorDataFetcher<List<Investment>>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "getActiveInvestments"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<List<Investment>> {
      return investmentRepository.getActiveInvestments()
          .map { list -> list.map { investmentMapper.mapToDto(it)}}
          .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
