package io.github.ackuq

import io.github.ackuq.configuration.DataSourceConfig
import io.github.ackuq.configuration.DatabaseFactory
import io.github.ackuq.configuration.applicationHttpClient
import io.github.ackuq.configuration.configureHTTP
import io.github.ackuq.configuration.configureOAuth
import io.github.ackuq.configuration.configureSerialization
import io.github.ackuq.configuration.configureStatusPages
import io.github.ackuq.routes.authenticationRoutes
import io.github.ackuq.routes.partyRoutes
import io.github.ackuq.routes.standpointsRoutes
import io.github.ackuq.routes.subjectRoutes
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.module(
    httpClient: HttpClient = applicationHttpClient,
) {
    DatabaseFactory.init(DataSourceConfig.fromApplicationConfig(environment.config))
    configureSerialization()
    configureHTTP()
    configureStatusPages()
    // OAuth
    configureOAuth(httpClient)
    // Resource routing
    install(Resources)
    // Default header
    install(DefaultHeaders)
    // Auto head response
    install(AutoHeadResponse)

    routing {
        route("/auth") {
            authenticationRoutes()
        }
        route("/api/v1") {
            standpointsRoutes()
            partyRoutes()
            subjectRoutes()
        }
    }
}
