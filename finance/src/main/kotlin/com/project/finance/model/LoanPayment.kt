package com.project.finance.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

data class LoanPayment(
    val id: String,
    val loanId: String,
    val paymentDate: LocalDate?,
    val amount: BigDecimal?,
    val createdAt:  OffsetDateTime?
)