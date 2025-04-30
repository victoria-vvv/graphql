package com.project.finance.model

import java.time.LocalDate

data class ExchangeRate(
    val id: String? = null,
    val baseCurrency: String?,
    val targetCurrency: String?,
    val rate: Double?,
    val rateDate: LocalDate
)