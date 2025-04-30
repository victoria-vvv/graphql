package com.project.finance.handler

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class GraphQLWebSocketHandler(private val graphQL: GraphQL, private val objectMapper: ObjectMapper): WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.receive()
            .flatMap { message ->
                val json = objectMapper.readValue(message.payloadAsText, GraphQLRequest::class.java)

                val input = ExecutionInput.newExecutionInput()
                    .query(json.query)
                    .operationName(json.operationName)
                    .variables(json.variables ?: emptyMap())
                    .build()

                return@flatMap Mono.fromCallable { graphQL.execute(input) }
                    .flatMapMany { executionResult ->
                        val publisher = executionResult.getData<Publisher<ExecutionResult>>()

                        Flux.from(publisher).map { result ->
                            val response = mapOf(
                                "type" to "next",
                                "payload" to mapOf("data" to result.getData<Any>())
                            )
                            session.textMessage(objectMapper.writeValueAsString(response))
                        }
                    }
                    .let { session.send(it) }
            }
            .then()
    }
}
