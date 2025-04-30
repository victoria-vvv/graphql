package com.project.finance.config

import com.project.finance.model.Transaction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks

@Configuration
class TransactionSinkConfig {

    @Bean
    fun transactionSinks(): Sinks.Many<Transaction>{
        return Sinks.many().multicast().onBackpressureBuffer()
    }
}