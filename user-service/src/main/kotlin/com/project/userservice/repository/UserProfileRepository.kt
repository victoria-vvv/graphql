package com.project.userservice.repository

import com.project.userservice.fetcher.node.log
import com.project.userservice.model.UserProfileInput
import com.project.userservice.model.tables.UserProfile.USER_PROFILE
import com.project.userservice.model.tables.records.UserProfileRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Repository
class UserProfileRepository(private val dslContext: DSLContext) {

    fun getUserProfile(userId: String): Mono<UserProfileRecord?> {
        return Mono.from (
          dslContext.selectFrom(USER_PROFILE)
                .where(USER_PROFILE.ID.eq(Integer.valueOf(userId))))
        }

    fun updateUserProfile(userProfileInput: UserProfileInput): Mono<UserProfileRecord> {
        val updatedUserProfile = dslContext.update(USER_PROFILE)

        if (userProfileInput.firstName != null) updatedUserProfile.set(USER_PROFILE.FIRST_NAME, userProfileInput.firstName)
        if (userProfileInput.lastName != null) updatedUserProfile.set(USER_PROFILE.LAST_NAME, userProfileInput.lastName)
        if (userProfileInput.email != null) updatedUserProfile.set(USER_PROFILE.EMAIL, userProfileInput.email)
        if (userProfileInput.language != null) updatedUserProfile.set(USER_PROFILE.LANGUAGE, userProfileInput.language)
        if (userProfileInput.notification != null) updatedUserProfile.set(USER_PROFILE.NOTIFICATION, userProfileInput.notification)

        return Mono.from(
            updatedUserProfile
                .set(USER_PROFILE.UPDATED_AT, OffsetDateTime.now())
                .where(USER_PROFILE.ID.eq(Integer.valueOf(userProfileInput.id)))
                .returning()
        )
    }

    fun getById(id: String): Mono<UserProfileRecord?> {
        log.info(" REPOSITORY RECEIVE ID $id")
        return Mono.from (
            dslContext.selectFrom(USER_PROFILE)
                .where(USER_PROFILE.ID.eq(Integer.valueOf(id)))
        )
    }
}