package io.github.ackuq.routes

import io.github.ackuq.configuration.OAuthConfiguration
import io.github.ackuq.models.dto.AuthInfo
import io.github.ackuq.routes.utils.handleApiSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.authenticationRoutes() {
    authenticate(OAuthConfiguration.oAuthName) {
        get("/login") {
            // Redirects to 'authorizeUrl' automatically
        }

        get("/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2 =
                call.principal() ?: throw BadRequestException("Invalid request")

            val authInfo = AuthInfo(accessToken = principal.accessToken)
            handleApiSuccess(authInfo, HttpStatusCode.OK, call)
        }
    }
}
