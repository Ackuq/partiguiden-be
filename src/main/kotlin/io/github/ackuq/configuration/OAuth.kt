package io.github.ackuq.configuration

import io.github.ackuq.dto.UserInfo
import io.github.ackuq.dto.UserSession
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.auth.session
import io.ktor.server.request.path
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import java.util.UUID

object OAuthConfiguration {
    const val name = "ouath"
    const val sessionName = "auth-session"
    const val provider = "http://localhost:8080/callback"
    const val stateQueryParameter = "state"
}

fun Application.configureOAuth() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }

    install(Authentication) {
        oauth(OAuthConfiguration.name) {
            urlProvider = {
                OAuthConfiguration.provider
            }
            providerLookup = {
                val state = this.request.queryParameters[OAuthConfiguration.stateQueryParameter]

                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = this@configureOAuth.environment.config.propertyOrNull("oauth.googleClientId")
                        ?.getString() ?: "",
                    clientSecret = this@configureOAuth.environment.config.propertyOrNull("oauth.googleClientSecret")
                        ?.getString() ?: "",
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.email"),
                    authorizeUrlInterceptor = {
                        // Add the state to authorize url
                        if (state != null) {
                            this.parameters[OAuthConfiguration.stateQueryParameter] = state
                        }
                    }
                )
            }
            client = applicationHttpClient
        }

        session<UserSession>(OAuthConfiguration.sessionName) {
            validate { session ->
                try {
                    val httpResponse = applicationHttpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer ${session.token}")
                        }
                    }
                    httpResponse.body() as UserInfo
                } catch (e: Exception) {
                    null
                }
            }
            challenge {
                // Store the users current location in the state cache, send the generated uuid to be able to look this up
                val uuid = UUID.randomUUID().toString()
                Storage.stateCache.put(
                    uuid,
                    Storage.State(redirectPath = call.request.path())
                )
                call.respondRedirect("/login?${OAuthConfiguration.stateQueryParameter}=$uuid")
            }
        }
    }
}
