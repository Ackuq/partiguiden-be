package io.github.ackuq.routes

import io.github.ackuq.resources.Standpoints
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route

fun Route.standpointsRoutes() {
    get<Standpoints> { standpoints ->
        call.respondText("Should return all standpoints")
    }
    post<Standpoints> {
        call.respondText("Should create standpoint", status = HttpStatusCode.Created)
    }
    get<Standpoints.Id> { standpoint ->
        call.respondText("Should return single standpoint")
    }
}