package com.project.finance.model

data class ExchangeRateInput(
    val baseCurrency: String?,
    val targetCurrency: String?,
    val rate: Double?
)