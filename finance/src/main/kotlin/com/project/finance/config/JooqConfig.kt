package com.project.finance.config

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
class JooqConfig(private val databaseClient: DatabaseClient) {

    @Bean
    fun dslContext(): DSLContext {
        return DSL.using(databaseClient.connectionFactory, SQLDialect.POSTGRES)
    }
}