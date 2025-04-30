package com.project.finance.config

import com.project.finance.handler.GraphQLHandler
import com.project.finance.handler.GraphQLWebSocketHandler
import com.project.finance.handler.GraphQlSseHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class GraphQLRouter(
    private val graphQLHandler: GraphQLHandler,
    private val graphQLWebSocketHandler: GraphQLWebSocketHandler,
    private val sseHandler: GraphQlSseHandler
) {

    @Bean
    fun router(): RouterFunction<ServerResponse> {
        return route()
            .GET("/graphql", graphQLHandler::schema)
            .POST("/graphql", graphQLHandler::request)
            .build()
    }

    @Bean
    fun handlerMapping(): HandlerMapping {
        val map = mapOf("/graphql-subscription" to graphQLWebSocketHandler)
        return SimpleUrlHandlerMapping(map, -1)
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

    @Bean
    fun sseRoute(): RouterFunction<ServerResponse> = router {
        POST("/sse-graphql", sseHandler::handle)
    }
}
