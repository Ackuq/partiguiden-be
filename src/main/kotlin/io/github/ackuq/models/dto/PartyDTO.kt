package io.github.ackuq.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class PartyDTO(
    val id: Int,
    val name: String,
    val abbreviation: String
)

@Serializable
data class NewPartyDTO(
    val name: String,
    val abbreviation: String
)

@Serializable
data class UpdatePartyDTO(
    val name: String?,
    val abbreviation: String?
)
