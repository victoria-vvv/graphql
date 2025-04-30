package com.project.finance.repository

import com.project.finance.model.tables.Investment.INVESTMENT
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository("InvestmentRepository")
class InvestmentRepository(private val dslContext: DefaultDSLContext): FinanceRepository {

    override fun supports(typeName: String): Boolean = typeName == "Investment"

    fun getActiveInvestments(): Mono<List<com.project.finance.model.tables.records.InvestmentRecord>> {
        return Flux.from(dslContext.selectFrom(INVESTMENT)
            .where(INVESTMENT.EXPIRATION_DATE.gt(LocalDate.now())))
            .collectList()
    }

    override fun getById(id: String): Mono<com.project.finance.model.tables.records.InvestmentRecord>? {
        return Mono.from(
            dslContext.selectFrom(INVESTMENT)
                .where(INVESTMENT.ID.eq(Integer.valueOf(id))))
    }
}