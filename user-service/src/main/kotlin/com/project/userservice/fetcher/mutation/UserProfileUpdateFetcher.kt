package com.project.userservice.fetcher.mutation

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.userservice.fetcher.FetcherImpl
import com.project.userservice.fetcher.ReactorDataFetcher
import com.project.userservice.mapper.UserProfileMapper
import com.project.userservice.model.UserProfile
import com.project.userservice.model.UserProfileInput
import com.project.userservice.repository.UserProfileRepository
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

@Component
class UserProfileUpdateFetcher(
    private val userProfileRepository: UserProfileRepository,
    private val objectMapper: ObjectMapper,
    private val userProfileMapper: UserProfileMapper
) : ReactorDataFetcher<UserProfile>, FetcherImpl {
    override val typeName = "Mutation"
    override val fieldName = "updateUserProfile"

    override fun async(environment: DataFetchingEnvironment): CompletableFuture<UserProfile> {
        val userInput = environment.getArgument<Map<String, Any>>("input")

        return Mono.fromCallable {
            objectMapper.convertValue(userInput, UserProfileInput::class.java)
        }
            .flatMap { userProfileInput ->
                userProfileRepository.updateUserProfile(userProfileInput)
            }.map { userProfile -> userProfileMapper.mapToUserProfile(userProfile) }
            .toFuture()
    }

    override fun getDataFetcher(): DataFetcher<*> = this
}

