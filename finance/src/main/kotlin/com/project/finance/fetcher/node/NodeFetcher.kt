package com.project.finance.fetcher.node

import com.project.finance.fetcher.FetcherImpl
import com.project.finance.fetcher.ReactorDataFetcher
import com.project.finance.mapper.CommonMapper
import com.project.finance.repository.*
import com.project.finance.util.GlobalIdUtils
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class NodeFetcher(
    private val repositories: List<FinanceRepository>,
    private val mappers: List<CommonMapper<*, *>>
) : ReactorDataFetcher<Any>, FetcherImpl {

    override val typeName = "Query"
    override val fieldName = "node"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<Any> {
        val globalId = environment.getArgument<String>("id")
        val (typeName, id) = GlobalIdUtils.fromGlobalId(globalId)

        if (typeName == "UserProfile") {
            return CompletableFuture.completedFuture(mapOf("__typename" to "UserProfile", "id" to id))
        }

        val repository = repositories.find { repo -> repo.supports(typeName) }
            ?: return CompletableFuture.completedFuture(null)

        return (repository.getById(id)
            ?.mapNotNull { mapToDto(it, typeName) }
            ?.doOnNext { println("NODE FETCHER RECEIVED REPOSITORY $it") }
            ?.toFuture()
            ?: CompletableFuture.completedFuture(null)) as CompletableFuture<Any>
    }

    private fun mapToDto(record: Any, typeName: String): Any? {
        val safeMappers = mappers.filterIsInstance<CommonMapper<Any, Any>>()
        val mapper = safeMappers.find { it.supports(typeName) }
        return mapper?.mapToDto(record)
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
