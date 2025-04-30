package com.project.finance.mapper

import com.project.finance.model.Loan
import com.project.finance.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class LoanMapper: CommonMapper<com.project.finance.model.tables.records.LoanRecord, Loan> {

    override fun mapToDto(record: com.project.finance.model.tables.records.LoanRecord): Loan {
        return Loan(
            id = GlobalIdUtils.toGlobalId("Loan", record.id.toString()),
            userId = record.userId.toString(),
            principalAmount = record.principalAmount,
            interestRate = record.interestRate,
            startDate = record.startDate,
            endDate = record.endDate,
            status = record.status,
            createdAt = record.createdAt
        )
    }

    override fun supports(typeName: String): Boolean {
        return typeName == "Loan"
    }
}
