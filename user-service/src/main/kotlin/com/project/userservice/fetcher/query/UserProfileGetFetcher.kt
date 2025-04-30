package com.project.userservice.fetcher.query

import com.project.userservice.fetcher.FetcherImpl
import com.project.userservice.fetcher.ReactorDataFetcher
import com.project.userservice.mapper.UserProfileMapper
import com.project.userservice.model.UserProfile
import com.project.userservice.repository.UserProfileRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class UserProfileGetFetcher(private val userProfileRepository: UserProfileRepository,
                            private val userProfileMapper: UserProfileMapper
) : ReactorDataFetcher<UserProfile>, FetcherImpl {
    override val typeName = "Query"
    override val fieldName = "getUserProfile"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<UserProfile> {
        val userId = environment.getArgument<String>("id")

        return if (userId != null) {
            userProfileRepository.getUserProfile(userId)
                .map { userProfileRecord ->
                    userProfileRecord?.let { userProfileMapper.mapToUserProfile(it) } }
                .toFuture()
        } else {
            CompletableFuture.completedFuture(null)
        }
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}
