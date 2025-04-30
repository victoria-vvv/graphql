package com.project.finance.auth

data class CustomGraphQLContext(
    val user: String?,
    val roles: Set<String>
)