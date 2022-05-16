package io.github.ackuq.routes.utils

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(val status: Int, val message: String)

@Serializable
data class ApiSuccess<T : Any>(val status: Int, val result: T)
