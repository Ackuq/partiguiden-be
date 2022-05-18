package io.github.ackuq.scrapers

import io.github.ackuq.models.dao.Party
import io.github.ackuq.models.dao.Standpoint
import io.github.ackuq.models.dto.NewStandpointDTO
import io.github.ackuq.models.dto.StandpointDTO
import io.github.ackuq.models.services.StandpointService

object ContentUpdater {
    private fun updateEntry(entry: ScrapedInformation, party: Party): Standpoint {
        // Check for any conflict
        val old = StandpointService.getStandpointByLink(entry.url)

        val standpointDTO = NewStandpointDTO(
            link = entry.url,
            title = entry.title,
            content = entry.content,
            paragraph = entry.paragraph,
            party = party.abbreviation,
            subject = old?.subject?.id?.value
        )
        // TODO: Generate diff instead of just eagerly updating
        return if (old != null) {
            StandpointService.updateStandpoint(
                old,
                standpointDTO
            )
        } else {
            StandpointService.createStandpoint(standpointDTO)
        }
    }

    fun updateContent(content: List<ScrapedInformation>, party: Party): List<StandpointDTO> {
        val updatedEntries = arrayListOf<StandpointDTO>()
        for (entry in content) {
            updateEntry(entry, party)
                .let { updatedEntries.add(it.toDTO()) }
        }
        return updatedEntries.toList()
    }
}
