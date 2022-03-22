package io.github.ackuq

import io.github.ackuq.configuration.configureHTTP
import io.github.ackuq.configuration.configureJWT
import io.github.ackuq.configuration.configureSerialization
import io.github.ackuq.configuration.configureStatusPages
import io.github.ackuq.routes.standpointsRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.resources.Resources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureJWT()
    configureStatusPages()
    install(Resources)

    routing {
        route("/api/v1") {
            standpointsRoutes()
        }
    }
}