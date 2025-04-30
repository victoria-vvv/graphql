package com.project.finance.fetcher.mutation

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.AccountMapper
import com.project.finance.model.Account
import com.project.finance.model.AccountInput
import com.project.finance.repository.AccountRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import reactor.core.publisher.Mono

@Component
class AccountCreateFetcher(private val accountRepository: AccountRepository,
                           private val accountMapper: AccountMapper,
                           private val objectMapper: ObjectMapper): ReactorDataFetcher<Account>, FetcherImpl {
    override val typeName = "Mutation"
    override val fieldName = "createAccount"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<Account> {
       val accountInput = environment.getArgument<Map<String, Any>>("input")

        return Mono.fromCallable {
            objectMapper.convertValue(accountInput, AccountInput::class.java)
        }
            .flatMap { accountRepository.createAccount(it) }
            .map {accountMapper.mapToDto(it) }
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
