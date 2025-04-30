package com.project.finance.mapper

import com.project.finance.model.Investment
import com.project.finance.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class InvestmentMapper: CommonMapper<com.project.finance.model.tables.records.InvestmentRecord, Investment> {

    override fun mapToDto(record: com.project.finance.model.tables.records.InvestmentRecord): Investment {
        return Investment(
            id = GlobalIdUtils.toGlobalId("Investment", record.id.toString()),
            userId = record.userId.toString(),
            investmentType = record.investmentType,
            amount = record.amount,
            startDate = record.startDate,
            expirationDate = record.expirationDate,
            interestRate = record.interestRate,
            createdAt = record.createdAt,
        )
    }

    override fun supports(typeName: String): Boolean {
        return typeName == "Investment"
    }
}
