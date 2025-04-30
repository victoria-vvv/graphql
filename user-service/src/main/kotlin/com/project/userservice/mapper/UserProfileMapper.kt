package com.project.userservice.mapper

import com.project.userservice.model.UserProfile
import com.project.userservice.model.tables.records.UserProfileRecord
import com.project.userservice.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class UserProfileMapper {

 fun mapToUserProfile(record: UserProfileRecord): UserProfile {
        return UserProfile(
            id = GlobalIdUtils.toGlobalId("UserProfile", record.id.toString()),
            email = record.email,
            passwordHash = record.passwordHash,
            language = record.language,
            notification = record.notification,
            firstName = record.firstName,
            lastName = record.lastName,
            createdAt = record.createdAt,
            updatedAt = record.updatedAt,
        )
    }
}