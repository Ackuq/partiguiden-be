package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class StandpointDTO(
    val id: Int,
    val link: String,
    val title: String,
    val content: List<String>,
    val updateDate: String,
    val party: String,
    val subject: Int?
)

@Serializable
data class UpdateStandpointDTO(
    val link: String?,
    val title: String?,
    val content: List<String>?,
    val party: String?,
    val subject: Int?
)

@Serializable
data class NewStandpointDTO(
    val link: String,
    val title: String,
    val content: List<String>,
    val party: String,
    val subject: Int?,
)

fun NewStandpointDTO.toUpdateDTO() = UpdateStandpointDTO(link, title, content, party, subject)
