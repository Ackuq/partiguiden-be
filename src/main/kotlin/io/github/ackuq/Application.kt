package io.github.ackuq

import io.github.ackuq.configuration.DatabaseFactory
import io.github.ackuq.configuration.IDatabaseFactory
import io.github.ackuq.configuration.configureHTTP
import io.github.ackuq.configuration.configureOAuth
import io.github.ackuq.configuration.configureSerialization
import io.github.ackuq.configuration.configureStatusPages
import io.github.ackuq.routes.authenticationRoutes
import io.github.ackuq.routes.partyRoutes
import io.github.ackuq.routes.standpointsRoutes
import io.github.ackuq.routes.subjectRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.AutoHeadResponse
import io.ktor.server.plugins.DefaultHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module(databaseFactory: IDatabaseFactory = DatabaseFactory) {
    configureSerialization()
    configureHTTP()
    configureStatusPages()
    // OAuth
    configureOAuth()
    // Resource routing
    install(Resources)
    // Default header
    install(DefaultHeaders)
    // Auto head response
    install(AutoHeadResponse)

    databaseFactory.init()

    routing {
        authenticationRoutes()
        route("/api/v1") {
            standpointsRoutes()
            partyRoutes()
            subjectRoutes()
        }
    }
}
