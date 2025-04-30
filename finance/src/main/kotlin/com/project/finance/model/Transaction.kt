package com.project.finance.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

data class Transaction(
    val id: String,
    val accountId: String,
    val amount: BigDecimal?,
    val transactionType: String?,
    val category: String?,
    val description: String?,
    val transactionDate:  LocalDate?,
    val createdAt:  OffsetDateTime?
)