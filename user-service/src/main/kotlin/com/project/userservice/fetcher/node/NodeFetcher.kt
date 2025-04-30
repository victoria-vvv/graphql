package com.project.userservice.fetcher.node

import com.project.userservice.fetcher.FetcherImpl
import com.project.userservice.fetcher.ReactorDataFetcher
import com.project.userservice.mapper.UserProfileMapper
import com.project.userservice.model.UserProfile
import com.project.userservice.repository.UserProfileRepository
import com.project.userservice.util.GlobalIdUtils
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

val log = LoggerFactory.getLogger("UserService")

@Component
class NodeFetcher(
    private val userProfileRepository: UserProfileRepository,
    private val userProfileMapper: UserProfileMapper
) : ReactorDataFetcher<UserProfile?>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "node"

    override fun getDataFetcher(): DataFetcher<*> = this

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<UserProfile?> {
        val globalId = environment.getArgument<String>("id")
        val (typeName, objectId) = GlobalIdUtils.fromGlobalId(globalId)

        return when (typeName) {
            "UserProfile" -> userProfileRepository.getById(objectId)
                .mapNotNull { it?.let(userProfileMapper::mapToUserProfile) }
                .toFuture()
            else -> CompletableFuture.completedFuture(null)
        }
    }
}