package com.project.finance.config

import com.project.finance.mapper.CommonMapper
import com.project.finance.model.*
import com.project.finance.repository.FinanceRepository
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class FederationEntityResolver(
    private val repositories: List<FinanceRepository>,
    private val mappers: List<CommonMapper<*, *>>
) {

    lateinit var graphQLSchema: GraphQLSchema

    fun resolveEntities(env: DataFetchingEnvironment): Mono<List<Any>> {
        val representations = env.getArgument<List<Map<String, Any>>>("representations")

        return Flux.fromIterable(representations)
            .flatMap { representation ->
                val typeName = representation["__typename"] as? String ?: return@flatMap Mono.empty<Any>()
                val repository = repositories.find { it.supports(typeName) }

                if (repository != null) {
                    val id = representation["id"] as? String ?: return@flatMap Mono.empty<Any>()
                    repository.getById(id)
                        ?.mapNotNull { record -> mapToDto(record, typeName) }
                        ?: Mono.empty()
                } else {
                    Mono.empty()
                }
            }
            .collectList()
            .map { list -> list.filterNotNull() }
    }

    fun resolveEntityType(value: Any): GraphQLObjectType? {
        return when (value) {
            is Account -> graphQLSchema.getObjectType("Account")
            is ExchangeRate -> graphQLSchema.getObjectType("ExchangeRate")
            is Investment -> graphQLSchema.getObjectType("Investment")
            is Loan -> graphQLSchema.getObjectType("Loan")
            is LoanPayment -> graphQLSchema.getObjectType("LoanPayment")
            is Transaction -> graphQLSchema.getObjectType("Transaction")
            else -> graphQLSchema.getObjectType("UserProfile")
        }
    }

    private fun mapToDto(record: Any, typeName: String): Any? {
        val safeMappers = mappers.filterIsInstance<CommonMapper<Any, Any>>()
        val mapper = safeMappers.find { it.supports(typeName) }
        return mapper?.mapToDto(record)
    }
}
