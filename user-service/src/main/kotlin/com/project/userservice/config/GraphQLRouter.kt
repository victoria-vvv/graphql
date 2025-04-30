package com.project.userservice.config

import com.project.userservice.handler.GraphQLHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RouterFunctions.route

@Configuration
class GraphQLRouter(
    private val graphQLHandler: GraphQLHandler
) {

    @Bean
    fun router(): RouterFunction<ServerResponse> {
        return route()
            .GET("/graphql", graphQLHandler::schema)
            .POST("/graphql", graphQLHandler::request)
            .build()
    }
}
