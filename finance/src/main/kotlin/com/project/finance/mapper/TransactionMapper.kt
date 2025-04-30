package com.project.finance.mapper

import com.project.finance.model.Transaction
import com.project.finance.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class TransactionMapper: CommonMapper<com.project.finance.model.tables.records.TransactionRecord, Transaction> {

    override fun mapToDto(record: com.project.finance.model.tables.records.TransactionRecord): Transaction {
        return Transaction(
            id = GlobalIdUtils.toGlobalId("Transaction", record.id.toString()),
            accountId = record.accountId.toString(),
            amount = record.amount,
            transactionType = record.transactionType,
            category = record.category,
            description = record.description,
            transactionDate = record.transactionDate,
            createdAt = record.createdAt
        )
    }

    override fun supports(typeName: String): Boolean {
        return typeName == "Transaction"
    }
}