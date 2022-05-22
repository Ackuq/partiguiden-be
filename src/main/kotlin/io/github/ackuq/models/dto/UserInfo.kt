package io.github.ackuq.models.dto

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String,
    val email: String,
    val verified_email: Boolean,
    val picture: String,
    val hd: String
) : Principal

@Serializable
data class AuthInfo(
    val accessToken: String,
)
