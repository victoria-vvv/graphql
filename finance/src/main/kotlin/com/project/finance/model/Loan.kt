package com.project.finance.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

data class Loan(
    val id: String,
    val userId: String,
    val principalAmount: BigDecimal?,
    val interestRate: Double?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val status: String?,
    val createdAt:  OffsetDateTime?
)