package com.project.userservice.config

import com.apollographql.federation.graphqljava.Federation
import com.project.userservice.fetcher.FetcherImpl
import com.project.userservice.model.UserProfile
import graphql.GraphQL
import graphql.language.StringValue
import graphql.scalars.ExtendedScalars
import graphql.schema.*
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.InputStreamReader
import java.math.BigDecimal

@Configuration
class GraphQLConfig(
    private val fetchers: List<FetcherImpl>,
    private val federationEntityResolver: FederationEntityResolver
) {

    @Bean
    fun runtimeWiring(
    ): RuntimeWiring {
        val wiringBuilder = RuntimeWiring.newRuntimeWiring()
            .scalar(ExtendedScalars.DateTime)
            .scalar(ExtendedScalars.Date)
            .scalar(bigDecimalScalar())

        val grouped = fetchers.groupBy { it.typeName }

        grouped["Query"]?.forEach {
            wiringBuilder.type("Query") { builder ->
                builder.dataFetcher(
                    it.fieldName, it.getDataFetcher())
            }
        }

        grouped["Mutation"]?.forEach {
            wiringBuilder.type("Mutation") { builder ->
                builder.dataFetcher(
                    it.fieldName, it.getDataFetcher())
            }
        }

        wiringBuilder.type("Node") {
            it.typeResolver { env ->
                when (env.getObject<Any>()) {
                    is UserProfile -> env.schema.getObjectType("UserProfile")
                    else -> null
                }
            }
        }

        return wiringBuilder.build()
    }

    @Bean
    fun typeDefinitionRegistry(): TypeDefinitionRegistry {
        val schemaInputStream = this::class.java
            .classLoader
            .getResourceAsStream("graphql/userProfileSchema.graphql")
            ?: throw IllegalStateException("Schema file not found in resources")

        return SchemaParser().parse(InputStreamReader(schemaInputStream))
    }

    @Bean
    fun graphQLSchema(): GraphQLSchema {
        val schema = Federation.transform(typeDefinitionRegistry(), runtimeWiring())
            .fetchEntities { env -> resolveEntities(env).subscribeOn(Schedulers.parallel())}
            .resolveEntityType { value -> resolveEntityType(value)  }
            .build()

        federationEntityResolver.graphQLSchema = schema

        return schema
    }

    @Bean
    @Primary
    fun graphQL(graphQLSchema: GraphQLSchema): GraphQL {
        return GraphQL.newGraphQL(graphQLSchema).build()
    }

    @Bean
    fun bigDecimalScalar(): GraphQLScalarType {
        return GraphQLScalarType.newScalar()
            .name("BigDecimal")
            .coercing(object : Coercing<BigDecimal, String> {
                override fun serialize(dataFetcherResult: Any): String {
                    return (dataFetcherResult as BigDecimal).toPlainString()
                }

                override fun parseValue(input: Any): BigDecimal {
                    return BigDecimal(input.toString())
                }

                override fun parseLiteral(input: Any): BigDecimal {
                    return BigDecimal((input as StringValue).value)
                }
            })
            .build()
    }

    private fun resolveEntities(env: DataFetchingEnvironment): Mono<List<UserProfile>> {
        return federationEntityResolver.resolveEntities(env)
    }

    private fun resolveEntityType(value: Any): GraphQLObjectType {
        return federationEntityResolver.resolveEntityType(value)
    }
}
