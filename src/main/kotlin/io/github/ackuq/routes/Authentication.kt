package io.github.ackuq.routes

import io.github.ackuq.configuration.OAuthConfiguration
import io.github.ackuq.configuration.Storage
import io.github.ackuq.dto.UserSession
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Route.authenticationRoutes() {
    authenticate(OAuthConfiguration.name) {
        get("/login") {
            // Redirects to 'authorizeUrl' automatically
        }

        get("/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            call.sessions.set(UserSession(principal?.accessToken.toString()))

            // Get the redirect state
            var respondPath = "/"
            val uuid = call.request.queryParameters[OAuthConfiguration.stateQueryParameter]

            uuid?.let {
                Storage.stateCache.getIfPresent(it)?.let { state -> respondPath = state.redirectPath }
            }

            call.respondRedirect(respondPath)
        }
    }
}
