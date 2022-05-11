package io.github.ackuq.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubjectDTO(
    val id: Int,
    val name: String,
    val standpoints: List<Int>
)

@Serializable
data class SubjectDetailedDTO(
    val id: Int,
    val name: String,
    val standpoints: List<StandpointDTO>
)

@Serializable
data class NewSubjectDTO(
    val name: String,
)

@Serializable
data class UpdateSubjectDTO(
    val name: String?,
)
