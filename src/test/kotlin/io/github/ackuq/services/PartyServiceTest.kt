package io.github.ackuq.services

import io.github.ackuq.models.dto.NewPartyDTO
import io.github.ackuq.models.services.PartyService
import io.github.ackuq.utils.TestExtension
import io.github.ackuq.utils.withTestServer
import io.ktor.server.plugins.BadRequestException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PartyServiceTest : TestExtension() {
    private val testParty = NewPartyDTO(name = "Test Party", abbreviation = "T")

    @Test
    fun createParty() = withTestServer {
        val party = PartyService.createParty(testParty)
        assertEquals(testParty.abbreviation, party.abbreviation)
        assertEquals(testParty.name, party.name)
    }

    @Test
    fun listParties() = withTestServer {
        PartyService.createParty(testParty)
        val parties = PartyService.getAllParties()
        assertEquals(1, parties.size)
        val party = parties[0]
        assertEquals(testParty.name, party.name)
        assertEquals(testParty.abbreviation, party.abbreviation)
    }

    @Test
    fun getParty() = withTestServer {
        PartyService.createParty(testParty)
        val party = PartyService.getPartyByAbbreviation(testParty.abbreviation)!!
        assertEquals(testParty.abbreviation, party.abbreviation)
        assertEquals(testParty.name, party.name)
    }

    @Test
    fun getPartyNotFound() = withTestServer {
        val nullParty1 = PartyService.getPartyByAbbreviation("error")
        assertEquals(null, nullParty1)
        val nullParty2 = PartyService.getPartyByName("error")
        assertEquals(null, nullParty2)
    }

    @Test
    fun noDuplicates() = withTestServer {
        PartyService.createParty(testParty)
        assertFailsWith(BadRequestException::class) {
            PartyService.createParty(NewPartyDTO(name = testParty.name, abbreviation = "unique"))
        }
        assertFailsWith(BadRequestException::class) {
            PartyService.createParty(NewPartyDTO(name = "unique", abbreviation = testParty.abbreviation))
        }
    }
}
