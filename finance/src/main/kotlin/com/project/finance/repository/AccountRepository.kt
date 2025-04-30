package com.project.finance.repository

import com.project.finance.model.AccountInput
import com.project.finance.model.tables.Account.ACCOUNT
import org.jooq.DSLContext
import org.jooq.Record2
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Repository("AccountRepository")
class AccountRepository(private var dslContext: DSLContext): FinanceRepository {

    override fun supports(typeName: String): Boolean = typeName == "Account"

    fun createAccount(accountInput: AccountInput): Mono<com.project.finance.model.tables.records.AccountRecord> {
        return Mono.from(
            dslContext.insertInto(ACCOUNT)
                .set(ACCOUNT.USER_ID, Integer.valueOf(accountInput.userId))
                .set(ACCOUNT.ACCOUNT_TYPE, accountInput.accountType)
                .set(ACCOUNT.CURRENCY, accountInput.currency)
                .returning()
        )
    }

    fun getAccountsBalances(userId: String): Mono<List<Record2<Int, BigDecimal>>> {
        return Flux.from(
            dslContext.select(ACCOUNT.ID, ACCOUNT.BALANCE)
                .from(ACCOUNT)
                .where(ACCOUNT.USER_ID.eq(Integer.valueOf(userId)))
        ).collectList()
    }

    override fun getById(id: String): Mono<com.project.finance.model.tables.records.AccountRecord>? {
        return Mono.from(
            dslContext.selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.eq(Integer.valueOf(id))))
        }
    }
