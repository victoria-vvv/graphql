package com.project.finance.mapper

import com.project.finance.model.LoanPayment
import com.project.finance.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class LoanPaymentMapper: CommonMapper<com.project.finance.model.tables.records.LoanPaymentRecord, LoanPayment> {

    override fun mapToDto (record: com.project.finance.model.tables.records.LoanPaymentRecord): LoanPayment {
        return LoanPayment(
            id = GlobalIdUtils.toGlobalId("LoanPayment", record.id.toString()),
            loanId = record.loanId.toString(),
            paymentDate = record.paymentDate,
            amount = record.amount,
            createdAt = record.createdAt,
        )
    }

    override fun supports(typeName: String): Boolean {
        return typeName == "LoanPayment"
    }
}
