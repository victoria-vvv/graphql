package com.project.finance.auth

import graphql.GraphQLException
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class AuthorizedDataFetcher<T>(
    private val delegate: DataFetcher<T>,
    private val requiresAuth: Boolean = false,
    private val allowedRoles: Set<String> = emptySet<String>()
) : DataFetcher<T> {

    override fun get(environment: DataFetchingEnvironment): T {
        val context = environment.getContext<com.project.finance.auth.CustomGraphQLContext>()
        val user = context.user

        if (requiresAuth && user == null) {
            throw GraphQLException("Unauthorized")
        }

        if (allowedRoles.isNotEmpty() && context.roles.none { it in allowedRoles }) {
            throw GraphQLException("Forbidden")
        }

        return delegate.get(environment)
    }
}