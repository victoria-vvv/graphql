package com.project.userservice.model

import java.io.Serializable
import java.time.OffsetDateTime

data class UserProfile(
    val id: String,
    var firstName: String?,
    var lastName: String?,
    var email: String?,
    var passwordHash: String?,
    var language: String?,
    var notification: Boolean?,
    val createdAt: OffsetDateTime?,
    var updatedAt: OffsetDateTime?
): Serializable