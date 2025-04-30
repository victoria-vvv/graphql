package com.project.finance.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class Account(
    val id: String,
    val userId: String,
    val accountType: String?,
    val currency: String?,
    var balance: BigDecimal?,
    val createdAt:  OffsetDateTime?,
    var updatedAt:  OffsetDateTime?
)