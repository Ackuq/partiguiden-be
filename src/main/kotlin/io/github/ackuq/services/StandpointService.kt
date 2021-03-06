package io.github.ackuq.services

import io.github.ackuq.dao.Standpoint
import io.github.ackuq.dao.Standpoints
import io.github.ackuq.dto.NewStandpointDTO
import io.github.ackuq.dto.UpdateStandpointDTO
import io.ktor.server.plugins.BadRequestException
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object StandpointService {
    fun getStandpoint(id: String): Standpoint? = transaction {
        Standpoint.find { Standpoints.id eq id }.firstOrNull()
    }

    fun getStandpointByLink(link: String): Standpoint? = transaction {
        Standpoint.find { Standpoints.link eq link }.firstOrNull()
    }

    fun getAllStandpoints(): List<Standpoint> = transaction {
        Standpoint.all().toList()
    }

    fun getStandpoints(abbreviations: List<String>): List<Standpoint> = transaction {
        Standpoint.find { Standpoints.id inList abbreviations }.toList()
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

    fun updateStandpoint(standpoint: Standpoint, updateStandpointDTO: UpdateStandpointDTO): Standpoint = transaction {
        updateStandpointDTO.title?.let { standpoint.title = it }
        updateStandpointDTO.content?.let { standpoint.content = it.toTypedArray() }
        // TODO: Updating link means updating the ID as well
        updateStandpointDTO.link?.let { standpoint.link = it }
        // Reference updates
        updateStandpointDTO.party?.let {
            val party = PartyService.getPartyByAbbreviation(it)
                ?: throw BadRequestException("Invalid party abbreviation $it")
            standpoint.party = party
        }
        updateStandpointDTO.subject?.let {
            val subject = SubjectService.getSubject(it)
            standpoint.subject = subject
        }
        standpoint
    }
}
