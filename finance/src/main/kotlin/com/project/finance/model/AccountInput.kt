package com.project.finance.model

data class AccountInput(
    val userId: String,
    val accountType: String,
    val currency: String
)