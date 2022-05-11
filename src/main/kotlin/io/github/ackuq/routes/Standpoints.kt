package io.github.ackuq.routes

import io.github.ackuq.configuration.OAuthConfiguration
import io.github.ackuq.dto.NewStandpointDTO
import io.github.ackuq.dto.UpdateStandpointDTO
import io.github.ackuq.resources.Standpoints
import io.github.ackuq.services.StandpointService
import io.github.ackuq.utils.handleApiSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route

fun Route.standpointsRoutes() {
    get<Standpoints> {
        val standpoints = StandpointService.getAllStandpoints()
        call.respondText("Should return all standpoints")
        handleApiSuccess(standpoints, HttpStatusCode.OK, call)
    }
    get<Standpoints.Id> {
        val standpoint = StandpointService.getStandpoint(it.id) ?: throw NotFoundException("Standpoint not found")
        handleApiSuccess(standpoint.toDTO(), HttpStatusCode.OK, call)
    }

    authenticate(OAuthConfiguration.authName) {
        post<Standpoints> {
            val standpointDTO = call.receive<NewStandpointDTO>()
            val standpoint = StandpointService.createStandpoint(standpointDTO)
            handleApiSuccess(standpoint.toDTO(), HttpStatusCode.Created, call)
        }
        patch<Standpoints.Id> {
            val standpoint =
                StandpointService.getStandpoint(it.id) ?: throw NotFoundException("Standpoint couldn't be found")
            val updateDTO = call.receive<UpdateStandpointDTO>()
            val newStandpoint = StandpointService.updateStandpoint(standpoint, updateDTO)
            handleApiSuccess(newStandpoint.toDTO(), HttpStatusCode.OK, call)
        }
    }
}
