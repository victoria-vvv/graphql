package com.project.userservice.handler

import graphql.ExecutionInput
import graphql.GraphQL
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

@Component
class GraphQLHandler(private val graphQL: GraphQL) {

    fun schema(request: ServerRequest): Mono<ServerResponse> {
        val schemaStream = javaClass.classLoader.getResourceAsStream("graphql/schema.graphqls")
            ?: return ServerResponse.notFound().build()

        val schemaText = schemaStream.bufferedReader().use { it.readText() }
        return ServerResponse.ok()
            .header("Content-Type", "text/plain")
            .bodyValue(schemaText)
    }

    fun request(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(GraphQLRequest::class.java)
            .flatMap { gqlRequest ->

                val executionInput = ExecutionInput.newExecutionInput()
                    .query(gqlRequest.query)
                    .operationName(gqlRequest.operationName)
                    .variables(gqlRequest.variables ?: emptyMap())
                    .build()

                val completableFuture = CompletableFuture.supplyAsync {
                    graphQL.execute(executionInput)
                }

                Mono.fromFuture { completableFuture }
                    .flatMap { result ->
                        val data = result.getData<Any?>()
                        val responseBody = mutableMapOf<String, Any?>("data" to data)

                        if (result.errors.isNotEmpty()) {
                            responseBody["errors"] = result.errors.map { it.toSpecification() }
                            return@flatMap ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue(responseBody)
                        } else {
                            return@flatMap ServerResponse.ok().bodyValue(responseBody)
                        }
                    }
            }
    }
}