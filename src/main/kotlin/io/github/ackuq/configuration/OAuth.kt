package io.github.ackuq.configuration

import io.github.ackuq.dto.UserInfo
import io.ktor.client.HttpClient
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

/**
 * Configuration values for OAuth
 */
object OAuthConfiguration {
    const val oAuthName = "oauth"
    const val authName = "auth-token"
    const val stateQueryParameter = "state"

    // URL for OAuth authentication
    const val authorizationURL = "https://accounts.google.com/o/oauth2/auth"

    // URL to get access token
    const val accessTokenURL = "https://accounts.google.com/o/oauth2/token"

    // URL to check token validity
    const val validationURL = "https://www.googleapis.com/oauth2/v2/userinfo"

    // Google uses URLs to specify scopes
    val scopes = listOf("https://www.googleapis.com/auth/userinfo.email")
}

fun Application.configureOAuth(httpClient: HttpClient) {
    install(Authentication) {
        oauth(OAuthConfiguration.oAuthName) {
            urlProvider = {
                this@configureOAuth.environment.config.propertyOrNull("oauth.callbackURL")?.getString() ?: ""
            }
            providerLookup = {
                val state = this.request.queryParameters[OAuthConfiguration.stateQueryParameter]

                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = OAuthConfiguration.authorizationURL,
                    accessTokenUrl = OAuthConfiguration.accessTokenURL,
                    requestMethod = HttpMethod.Post,
                    clientId = this@configureOAuth.environment.config.propertyOrNull("oauth.googleClientId")
                        ?.getString() ?: "",
                    clientSecret = this@configureOAuth.environment.config.propertyOrNull("oauth.googleClientSecret")
                        ?.getString() ?: "",
                    defaultScopes = OAuthConfiguration.scopes,
                    authorizeUrlInterceptor = {
                        // Add the state to authorize url
                        if (state != null) {
                            this.parameters[OAuthConfiguration.stateQueryParameter] = state
                        }
                    }
                )
            }
            client = httpClient
        }
        token(OAuthConfiguration.authName) {
            validate { token ->
                // TODO: Should not mock this here
                val isTest = this@configureOAuth.environment.config.propertyOrNull("ktor.environment")?.getString()
                    .equals("test")
                if (isTest) {
                    UserInfo("1", "test", true, "", "")
                } else {
                    try {
                        val httpResponse = httpClient.get(OAuthConfiguration.validationURL) {
                            headers {
                                append(HttpHeaders.Authorization, "Bearer $token")
                            }
                        }
                        httpResponse.body<UserInfo>()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }
}
