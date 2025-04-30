package com.project.finance.config

import com.apollographql.federation.graphqljava.Federation
import com.project.finance.fetcher.FetcherImpl
import com.project.finance.model.*
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

        grouped["Subscription"]?.forEach {
            wiringBuilder.type("Subscription") { builder ->
                builder.dataFetcher(
                    it.fieldName, it.getDataFetcher())
            }
        }

        wiringBuilder.type("Node") {
            it.typeResolver { env ->
                when (val obj = env.getObject<Any>()) {
                    is Account -> env.schema.getObjectType("Account")
                    is ExchangeRate -> env.schema.getObjectType("ExchangeRate")
                    is Investment -> env.schema.getObjectType("Investment")
                    is Loan -> env.schema.getObjectType("Loan")
                    is LoanPayment -> env.schema.getObjectType("LoanPayment")
                    is Transaction -> env.schema.getObjectType("Transaction")
                    else -> env.schema.getObjectType("UserProfile")
                }
            }
        }

        return wiringBuilder.build()
    }

    @Bean
    fun typeDefinitionRegistry(): TypeDefinitionRegistry {
        val schemaInputStream = this::class.java
            .classLoader
            .getResourceAsStream("graphql/financeSchema.graphql")
            ?: throw IllegalStateException("Schema file not found in resources")

        return SchemaParser().parse(InputStreamReader(schemaInputStream))
    }

    @Bean
    fun graphQLSchema(): GraphQLSchema {
        val schema = Federation.transform(typeDefinitionRegistry(), runtimeWiring())
            .fetchEntities { env -> federationEntityResolver.resolveEntities(env) }
            .resolveEntityType { value -> federationEntityResolver.resolveEntityType(value) }
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
}
