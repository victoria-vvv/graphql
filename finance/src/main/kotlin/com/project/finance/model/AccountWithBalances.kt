package com.project.finance.model

import java.math.BigDecimal

data class AccountWithBalances (
    val accountId: String,
    var balance: BigDecimal
)