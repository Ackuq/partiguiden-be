package io.github.ackuq.models.services

import io.github.ackuq.models.dao.Party
import io.github.ackuq.models.dao.Standpoint
import io.github.ackuq.models.dao.StandpointDeleteEvent
import io.github.ackuq.models.dao.StandpointUpdateEvent
import io.github.ackuq.models.dao.Standpoints
import io.github.ackuq.models.dto.NewStandpointDTO
import io.ktor.server.plugins.BadRequestException
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object StandpointService {
    fun getStandpoint(id: Int): Standpoint? = transaction {
        Standpoint.findById(id)
    }

    fun getStandpointByLink(link: String): Standpoint? = transaction {
        Standpoint.find { Standpoints.link eq link }.firstOrNull()
    }

    fun getAllStandpoints(): List<Standpoint> = transaction {
        Standpoint.all().toList()
    }

    fun createStandpoint(newStandpointDTO: NewStandpointDTO): Standpoint = transaction {
        if (getStandpointByLink(newStandpointDTO.link) != null) {
            throw BadRequestException("Standpoint with this link already exists")
        }
        val partyValue = PartyService.getPartyByAbbreviation(newStandpointDTO.party)
            ?: throw BadRequestException("Invalid party abbreviation ${newStandpointDTO.party}")
        val subjectValue =
            newStandpointDTO.subject?.let { SubjectService.getSubject(it) }

        Standpoint.new {
            link = newStandpointDTO.link
            title = newStandpointDTO.title
            content = newStandpointDTO.content.toTypedArray()
            updateDate = LocalDateTime.now()
            party = partyValue
            subject = subjectValue
        }
    }

    data class StandpointUpdatePayload(
        val newLink: String,
        val newTitle: String,
        val newContent: List<String>,
        val newParagraph: String?,
        val party: Party,
        val standpoint: Standpoint?,
    )

    fun createUpdateEvent(payload: StandpointUpdatePayload): StandpointUpdateEvent = transaction {
        StandpointUpdateEvent.new {
            newLink = payload.newLink
            newTitle = payload.newTitle
            newContent = payload.newContent.toTypedArray()
            newParagraph = payload.newParagraph
            updateDate = LocalDateTime.now()
            party = payload.party
            standpoint = payload.standpoint
        }
    }

    fun createDeleteEvent(standpoint: Standpoint): StandpointDeleteEvent = transaction {
        StandpointDeleteEvent.new {
            this@new.standpoint = standpoint
        }
    }

    fun updateStandpoint(standpoint: Standpoint, newValues: NewStandpointDTO): Standpoint = transaction {
        /**
         * Note: Update date should not be updated here, as it should only be replaced when new scraped data is detected
         */
        standpoint.title = newValues.title
        standpoint.content = newValues.content.toTypedArray()
        standpoint.paragraph = newValues.paragraph
        standpoint.link = newValues.link
        // Reference updates
        standpoint.party = newValues.party.let {
            PartyService.getPartyByAbbreviation(it) ?: throw BadRequestException("Invalid party abbreviation $it")
        }
        standpoint.subject = newValues.subject?.let {
            SubjectService.getSubject(it)
        }
        standpoint
    }
}
