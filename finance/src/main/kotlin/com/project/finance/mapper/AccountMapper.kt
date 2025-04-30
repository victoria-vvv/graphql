package com.project.finance.mapper

import com.project.finance.model.Account
import com.project.finance.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class AccountMapper: CommonMapper<com.project.finance.model.tables.records.AccountRecord, Account> {

    override fun mapToDto(record: com.project.finance.model.tables.records.AccountRecord): Account {
        return Account(
            id = GlobalIdUtils.toGlobalId("Account", record.id.toString()),
            userId = record.userId.toString(),
            accountType = record.accountType,
            currency = record.currency,
            balance = record.balance,
            createdAt = record.createdAt,
            updatedAt = record.updatedAt,
        )
    }

    override fun supports(typeName: String): Boolean {
        return typeName == "Account"
    }
}