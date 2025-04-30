package com.project.finance.repository

import com.project.finance.model.tables.LoanPayment.LOAN_PAYMENT
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDate

@Repository("LoanPaymentRepository")
class LoanPaymentRepository(private val dslContext: DSLContext): FinanceRepository {

    override fun supports(typeName: String): Boolean = typeName == "LoanPayment"

    fun createLoanPayment(loanId: String, amount: BigDecimal): Mono<com.project.finance.model.tables.records.LoanPaymentRecord> {
        return Mono.from(dslContext.insertInto(LOAN_PAYMENT)
            .set(LOAN_PAYMENT.LOAN_ID, Integer.valueOf(loanId))
            .set(LOAN_PAYMENT.PAYMENT_DATE, LocalDate.now())
            .set(LOAN_PAYMENT.AMOUNT, amount)
            .returning()
        )
    }

    override fun getById(id: String): Mono<com.project.finance.model.tables.records.LoanPaymentRecord>? {
        return Mono.from(
            dslContext.selectFrom(LOAN_PAYMENT)
                .where(LOAN_PAYMENT.ID.eq(Integer.valueOf(id))))
    }
}