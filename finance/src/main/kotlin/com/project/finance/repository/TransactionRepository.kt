package com.project.finance.repository

import com.project.finance.model.TransactionInput
import com.project.finance.model.tables.Transaction.TRANSACTION
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDate

@Repository("TransactionRepository")
class TransactionRepository(private val dslContext: DSLContext): FinanceRepository {

    override fun supports(typeName: String): Boolean = typeName == "Transaction"

    fun getTransactionsInPeriodByCategories(startDate: LocalDate, endDate: LocalDate, category: String):
            Mono<List<com.project.finance.model.tables.records.TransactionRecord>> {
        return Flux.from(
            dslContext.selectFrom(TRANSACTION)
                .where(
                    TRANSACTION.TRANSACTION_DATE.between(startDate, endDate),
                    TRANSACTION.CATEGORY.eq(category)
                )
        )
            .collectList()
    }

    fun createTransaction(transactionInput: TransactionInput): Mono<com.project.finance.model.tables.records.TransactionRecord> {
        return Mono.from(
            dslContext.insertInto(TRANSACTION)
                .set(TRANSACTION.ACCOUNT_ID, Integer.valueOf(transactionInput.accountId))
                .set(TRANSACTION.AMOUNT, transactionInput.amount)
                .set(TRANSACTION.TRANSACTION_TYPE, transactionInput.transactionType)
                .set(TRANSACTION.CATEGORY, transactionInput.category)
                .set(TRANSACTION.DESCRIPTION, transactionInput.description)
                .returning()
        )
    }

    fun getAverageSpendingByPeriod(period: String, transactionType: String): Mono<BigDecimal> {
        val groupByField = when (period) {
            "day" -> DSL.field("date_trunc('day', transaction_date)", LocalDate::class.java)
            "week" -> DSL.field("date_trunc('week', transaction_date)", LocalDate::class.java)
            "month" -> DSL.field("date_trunc('month', transaction_date)", LocalDate::class.java)
            else -> throw IllegalArgumentException("Unsupported period: $period")
        }

        val query = dslContext
            .select(DSL.avg(TRANSACTION.AMOUNT).`as`("avg_amount"))
            .from(TRANSACTION)
            .where(
                TRANSACTION.AMOUNT.isNotNull,
                TRANSACTION.TRANSACTION_TYPE.eq(transactionType)
            )
            .groupBy(groupByField)
            .orderBy(groupByField.desc())
            .limit(1)

        return Mono.from(query)
            .map { record -> record.get("avg_amount", BigDecimal::class.java) ?: BigDecimal.ZERO }
    }

    fun getTransactionsOnPageByFilter(
        accountId: String,
        page: Int,
        size: Int,
        createdDate: LocalDate,
        category: String
    ):
            Mono<List<com.project.finance.model.tables.records.TransactionRecord>> {
        val offset = page * size

        return Flux.from(
            dslContext.selectFrom(TRANSACTION)
                .where(
                    TRANSACTION.ACCOUNT_ID.eq(Integer.valueOf(accountId))
                        .and(TRANSACTION.CATEGORY.eq(category))
                        .and(TRANSACTION.CREATED_AT.cast(LocalDate::class.java).eq(createdDate))
                )
                .orderBy(TRANSACTION.CREATED_AT.desc())
                .limit(size)
                .offset(offset)
        )
            .collectList()
    }

    override fun getById(id: String): Mono<com.project.finance.model.tables.records.TransactionRecord>? {
        return Mono.from(
            dslContext.selectFrom(TRANSACTION)
                .where(TRANSACTION.ID.eq(Integer.valueOf(id))))
    }
}