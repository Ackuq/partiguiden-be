package io.github.ackuq.services

import io.github.ackuq.dao.Parties
import io.github.ackuq.dao.Party
import io.github.ackuq.dto.NewPartyDTO
import io.github.ackuq.dto.UpdatePartyDTO
import io.ktor.server.plugins.BadRequestException
import org.jetbrains.exposed.sql.transactions.transaction

object PartyService {
    fun getPartyByAbbreviation(abbreviation: String): Party? = transaction {
        Party.find { Parties.abbreviation eq abbreviation }.firstOrNull()
    }

    fun getPartyByName(name: String): Party? = transaction {
        Party.find { Parties.name eq name }.firstOrNull()
    }

    fun getAllParties(): List<Party> = transaction {
        Party.all().toList()
    }

    fun getParties(abbreviations: List<String>): List<Party> = transaction {
        Party.find { Parties.abbreviation inList abbreviations }.toList()
    }

    fun createParty(newPartyDTO: NewPartyDTO): Party = transaction {
        if (getPartyByAbbreviation(newPartyDTO.abbreviation) != null) {
            throw BadRequestException("Party with this abbreviation already exists")
        }
        if (getPartyByName(newPartyDTO.name) != null) {
            throw BadRequestException("Party with this name already exists")
        }

        Party.new {
            name = newPartyDTO.name
            abbreviation = newPartyDTO.abbreviation
        }
    }

    fun updateParty(party: Party, newData: UpdatePartyDTO): Party = transaction {
        newData.name?.let { party.name = it }
        newData.abbreviation?.let { party.abbreviation = it }
        party
    }
}
