package com.project.finance.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

data class Investment(
    val id: String,
    val userId: String,
    val investmentType: String?,
    val amount: BigDecimal?,
    val startDate: LocalDate?,
    val expirationDate: LocalDate?,
    val interestRate: Double?,
    val createdAt:  OffsetDateTime?
)