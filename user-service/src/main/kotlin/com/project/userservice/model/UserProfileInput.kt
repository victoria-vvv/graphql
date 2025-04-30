package com.project.userservice.model

data class UserProfileInput (
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val language: String?,
    val notification: Boolean?
)
