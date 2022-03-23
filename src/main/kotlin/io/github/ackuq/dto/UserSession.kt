package io.github.ackuq.dto

import io.ktor.server.auth.Principal
import kotlinx.serialization.Serializable

data class UserSession(val token: String)

@Serializable
data class UserInfo(
    val id: String,
    val email: String,
    val verified_email: Boolean,
    val picture: String,
    val hd: String
) : Principal
