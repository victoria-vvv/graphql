package com.project.finance.handler

import com.project.finance.model.ExchangeRate
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class GraphQlSseHandler(
    private val graphQL: GraphQL
) {

    fun handle(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(GraphQLRequest::class.java)
            .flatMapMany { graphQLRequest ->
                val executionInput = ExecutionInput.newExecutionInput()
                    .query(graphQLRequest.query)
                    .operationName(graphQLRequest.operationName)
                    .variables(graphQLRequest.variables ?: emptyMap())
                    .build()

                Mono.fromFuture(graphQL.executeAsync(executionInput))
                    .flatMapMany { executionResult ->
                        val publisher = executionResult.getData<Publisher<ExecutionResult>>()
                        Flux.from(publisher)
                    }
                    .map { result ->
                        ServerSentEvent.builder<ExchangeRate>()
                            .event("data")
                            .data(result.getData<ExchangeRate>())
                            .id(UUID.randomUUID().toString())
                            .build()
                    }
            }
            .let { flux ->
                ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(flux, ServerSentEvent::class.java)
            }
    }
}

