package io.github.ackuq.scrapers

import io.github.ackuq.configuration.applicationHttpClient
import io.github.ackuq.models.dao.Party
import io.github.ackuq.models.dao.StandpointDeleteEvent
import io.github.ackuq.models.dao.StandpointUpdateEvent
import io.github.ackuq.models.dto.StandpointUpdateEventDTO
import io.github.ackuq.models.services.StandpointService
import io.ktor.client.request.get
import io.ktor.http.isSuccess

object ContentUpdater {
    private fun updateEntry(entry: ScrapedInformation, party: Party): StandpointUpdateEvent {
        // Check for any conflict
        val standpoint = StandpointService.getStandpointByLink(entry.url)

        val standpointUpdateEvent = StandpointService.StandpointUpdatePayload(
            newLink = entry.url,
            newTitle = entry.title,
            newContent = entry.content,
            newParagraph = entry.paragraph,
            party = party,
            standpoint = standpoint
        )
        return StandpointService.createUpdateEvent(standpointUpdateEvent)
    }

    fun updateContent(content: List<ScrapedInformation>, party: Party): List<StandpointUpdateEventDTO> {
        val updatedEntries = arrayListOf<StandpointUpdateEventDTO>()
        for (entry in content) {
            updateEntry(entry, party)
                .let { updatedEntries.add(it.toDTO()) }
        }
        return updatedEntries.toList()
    }

    suspend fun pruneOld(): List<StandpointDeleteEvent> {
        val deleteEvents = arrayListOf<StandpointDeleteEvent>()
        val standpoints = StandpointService.getAllStandpoints()
        for (standpoint in standpoints) {
            val request = applicationHttpClient.get(standpoint.link)
            if (!request.status.isSuccess()) {
                deleteEvents.add(
                    StandpointService.createDeleteEvent(standpoint)
                )
            }
        }
        return deleteEvents.toList()
    }
}
