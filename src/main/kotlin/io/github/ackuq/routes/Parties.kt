package io.github.ackuq.routes

import io.github.ackuq.configuration.OAuthConfiguration
import io.github.ackuq.models.dto.NewPartyDTO
import io.github.ackuq.models.dto.UpdatePartyDTO
import io.github.ackuq.models.services.PartyService
import io.github.ackuq.routes.resources.Parties
import io.github.ackuq.routes.utils.handleApiSuccess
import io.github.ackuq.scrapers.ContentUpdater
import io.github.ackuq.scrapers.getScraper
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.routing.Route

fun Route.partyRoutes() {

    get<Parties> {
        val parties = PartyService.getAllParties().map { it.toDTO() }
        handleApiSuccess(parties, HttpStatusCode.OK, call)
    }
    get<Parties.Abbreviation> {
        val party = PartyService.getPartyByAbbreviation(it.abbreviation)
            ?: throw NotFoundException("Party could not be found")
        handleApiSuccess(party.toDTO(), HttpStatusCode.OK, call)
    }
    authenticate(OAuthConfiguration.authName) {
        post<Parties> {
            val newParty = call.receive<NewPartyDTO>()
            val party = PartyService.createParty(newParty)
            handleApiSuccess(party.toDTO(), HttpStatusCode.Created, call)
        }
        post<Parties.Abbreviation.Scrape> {
            val party = PartyService.getPartyByAbbreviation(it.parent.abbreviation)
                ?: throw NotFoundException("Party could not be found")
            val scraper = getScraper(party.abbreviation)
            val content = scraper.getPages()
            val updatedEntries = ContentUpdater.updateContent(content, party)
            handleApiSuccess(updatedEntries, HttpStatusCode.OK, call)
        }
        post<Parties.Abbreviation.Scrape.Dry> {
            val party = PartyService.getPartyByAbbreviation(it.parent.parent.abbreviation)
                ?: throw NotFoundException("Party could not be found")
            val scraper = getScraper(party.abbreviation)
            val content = scraper.getPages(it.max)
            handleApiSuccess(content, HttpStatusCode.OK, call)
        }
        patch<Parties.Abbreviation> {
            val party = PartyService.getPartyByAbbreviation(it.abbreviation)
                ?: throw NotFoundException("Party could not be found")
            val updatePartyDTO = call.receive<UpdatePartyDTO>()
            val updatedParty = PartyService.updateParty(party, updatePartyDTO)
            handleApiSuccess(updatedParty.toDTO(), HttpStatusCode.OK, call)
        }
        delete<Parties.Abbreviation> {
            val party = PartyService.getPartyByAbbreviation(it.abbreviation)
                ?: throw NotFoundException("Party could not be found")
            PartyService.deleteParty(party)
            handleApiSuccess("Successfully deleted party", HttpStatusCode.OK, call)
        }
    }
}
