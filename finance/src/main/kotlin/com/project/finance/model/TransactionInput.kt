package com.project.finance.model

import java.math.BigDecimal

data class TransactionInput (
    val accountId: String,
    val amount: BigDecimal,
    val transactionType: String,
    val category: String,
    val description: String
)