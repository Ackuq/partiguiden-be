package io.github.ackuq.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class StandpointDTO(
    val id: Int,
    val link: String,
    val title: String,
    val content: List<String>,
    val paragraph: String?,
    val updateDate: String,
    val party: String,
    val subject: Int?
)

@Serializable
data class NewStandpointDTO(
    val link: String,
    val title: String,
    val content: List<String>,
    val paragraph: String?,
    val party: String,
    val subject: Int?,
)
