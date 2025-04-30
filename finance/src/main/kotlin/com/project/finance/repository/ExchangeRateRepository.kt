package com.project.finance.repository

import com.project.finance.model.ExchangeRateInput
import com.project.finance.model.tables.ExchangeRate.EXCHANGE_RATE
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository("ExchangeRateRepository")
class ExchangeRateRepository(private var dslContext: DSLContext): FinanceRepository {

    override fun supports(typeName: String): Boolean = typeName == "ExchangeRate"

    fun createExchangeRate(input: ExchangeRateInput): Mono<com.project.finance.model.tables.records.ExchangeRateRecord> {
        return Mono.from(
            dslContext.insertInto(EXCHANGE_RATE)
                .set(EXCHANGE_RATE.BASE_CURRENCY, input.baseCurrency)
                .set(EXCHANGE_RATE.TARGET_CURRENCY, input.targetCurrency)
                .set(EXCHANGE_RATE.RATE, input.rate)
                .returning()
        )
    }

    fun getExchangeRates(baseCurrency: String, targetCurrency: String, startDate: LocalDate, endDate: LocalDate):
            Mono<List<com.project.finance.model.tables.records.ExchangeRateRecord>> {

        return Flux.from(
                dslContext.selectFrom(EXCHANGE_RATE)
                    .where(
                        EXCHANGE_RATE.BASE_CURRENCY.eq(baseCurrency),
                        EXCHANGE_RATE.TARGET_CURRENCY.eq(targetCurrency),
                        EXCHANGE_RATE.RATE_DATE.between(startDate, endDate)
                    )
            )
                .collectList()
        }

    override fun getById(id: String): Mono<com.project.finance.model.tables.records.ExchangeRateRecord>? {
        return Mono.from(
            dslContext.selectFrom(EXCHANGE_RATE)
                .where(EXCHANGE_RATE.ID.eq(Integer.valueOf(id))))
    }
    }
