package com.project.finance.config

import com.project.finance.model.ExchangeRate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks

@Configuration
class ExchangeRateSinkConfig {

    @Bean
    fun exchangeRateSink(): Sinks.Many<ExchangeRate> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }
}