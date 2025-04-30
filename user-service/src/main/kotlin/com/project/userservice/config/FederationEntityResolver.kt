package com.project.userservice.config

import com.project.userservice.mapper.UserProfileMapper
import com.project.userservice.model.UserProfile
import com.project.userservice.repository.UserProfileRepository
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class FederationEntityResolver(
    private val userProfileRepository: UserProfileRepository,
    private val userProfileMapper: UserProfileMapper
) {

    lateinit var graphQLSchema: GraphQLSchema


    fun resolveEntities(env: DataFetchingEnvironment): Mono<List<UserProfile>> {
        val representations = env.getArgument<List<Map<String, Any>>>("representations")

        return Flux.fromIterable(representations)
            .flatMap { representation ->
                when (representation["__typename"]) {
                    "UserProfile" -> {
                        val id = representation["id"] as? String
                        if (id != null) {
                            userProfileRepository.getUserProfile(id)
                                .mapNotNull { record -> record?.let { userProfileMapper.mapToUserProfile(it) } }
                        } else {
                            Mono.empty()
                        }
                    }
                    else -> Mono.empty()
                }
            }
            .collectList()
    }

    fun resolveEntityType(value: Any): GraphQLObjectType {
        return when (value) {
            is UserProfile -> graphQLSchema.getObjectType("UserProfile")
            else -> throw IllegalArgumentException("Unknown type: ${value::class.simpleName}")
        }
    }
}
