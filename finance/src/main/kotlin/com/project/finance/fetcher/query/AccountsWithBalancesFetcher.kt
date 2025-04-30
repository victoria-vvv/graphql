package com.project.finance.fetcher.query

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.model.AccountWithBalances
import com.project.finance.repository.AccountRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class AccountsWithBalancesFetcher(private val accountRepository: AccountRepository):
ReactorDataFetcher<List<AccountWithBalances>>, FetcherImpl{
    override val typeName = "Query"
    override val fieldName = "getAccountsWithBalances"

    override val requiresAuth = true
    override val allowedRoles = setOf("ADMIN", "FINANCE")

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<List<AccountWithBalances>> {
       val userId = environment.getArgument<String>("userId")

        return accountRepository.getAccountsBalances(userId)
            .map { list  -> list.map{ AccountWithBalances(
                accountId = it.value1().toString(),
                balance = it.value2()
                ) }}
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
