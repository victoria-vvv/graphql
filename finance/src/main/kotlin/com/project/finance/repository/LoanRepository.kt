package com.project.finance.repository

import com.project.finance.model.tables.LoanPayment.LOAN_PAYMENT
import com.project.finance.model.tables.Loan.LOAN
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository("LoanRepository")
class LoanRepository(private val dslContext: DSLContext): FinanceRepository {

    override fun supports(typeName: String): Boolean = typeName == "Loan"

    fun getLoanPaymentsForActiveLoans(userId: String): Mono<List<com.project.finance.model.tables.records.LoanPaymentRecord>> {
        val query = dslContext.select(LOAN_PAYMENT.asterisk())
            .from(LOAN_PAYMENT)
            .join(LOAN).on(LOAN.ID.eq(LOAN_PAYMENT.LOAN_ID))
            .where(
                LOAN.USER_ID.eq(Integer.valueOf(userId))
                    .and(LOAN.END_DATE.gt(LocalDate.now()))
            )

        return Flux.from(query).map { record -> record.into(LOAN_PAYMENT) }.collectList()
    }

    override fun getById(id: String): Mono<com.project.finance.model.tables.records.LoanRecord>? {
        return Mono.from(
            dslContext.selectFrom(LOAN)
                .where(LOAN.ID.eq(Integer.valueOf(id))))
    }
}