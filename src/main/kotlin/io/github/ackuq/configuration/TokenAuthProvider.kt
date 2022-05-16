package io.github.ackuq.configuration

import io.github.ackuq.utils.handleApiError
import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.AuthenticationFunction
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.Principal
import io.ktor.server.auth.parseAuthorizationHeader
import io.ktor.server.request.ApplicationRequest

class TokenAuthProvider private constructor(config: Config) : AuthenticationProvider(config) {
    internal val validatorFunction = config.validator

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val token = call.request.tokenAuthentication()
        val principal = token?.let { validatorFunction(call, it) }

        if (principal != null) {
            context.principal(principal)
        } else {
            val cause = when (token) {
                null -> AuthenticationFailedCause.NoCredentials
                else -> AuthenticationFailedCause.InvalidCredentials
            }
            @Suppress("NAME_SHADOWING")
            context.challenge(TokenAuthChallengeKey, cause) { challenge, call ->
                handleApiError("Not authenticated", HttpStatusCode.Unauthorized, call)
                challenge.complete()
            }
        }
    }

    class Config(name: String?) : AuthenticationProvider.Config(name) {
        internal var validator: AuthenticationFunction<String> = UninitializedValidator

        fun validate(block: AuthenticationFunction<String>) {
            check(validator === UninitializedValidator) { "Only one validator could be registered" }
            validator = block
        }

        private fun verifyConfiguration() {
            check(validator !== UninitializedValidator) {
                "It should be a validator supplied to a token auth provider"
            }
        }

        @PublishedApi
        internal fun buildProvider(): TokenAuthProvider {
            verifyConfiguration()
            return TokenAuthProvider(this)
        }
    }

    companion object {
        private val UninitializedValidator: suspend ApplicationCall.(Any) -> Principal? = {
            error("It should be a validator supplied to a token auth provider")
        }
    }
}

inline fun AuthenticationConfig.token(
    name: String? = null,
    configure: TokenAuthProvider.Config.() -> Unit
) {
    val provider = TokenAuthProvider.Config(name).apply(configure).buildProvider()
    register(provider)
}

/**
 * A context for [TokenAuthChallengeFunction].
 */
class TokenChallengeContext

/**
 * Specifies what to send back if token authentication fails.
 */
typealias TokenAuthChallengeFunction = suspend TokenChallengeContext.(String?) -> Unit

/**
 * Retrieves [token] authentication credentials for this [ApplicationRequest].
 */
fun ApplicationRequest.tokenAuthentication(): String? {
    when (val authHeader = parseAuthorizationHeader()) {
        is HttpAuthHeader.Single -> {
            if (!authHeader.authScheme.equals("Bearer", ignoreCase = true)) {
                return null
            }

            return authHeader.blob
        }
        else -> return null
    }
}

/**
 * A key used to register authentication challenge.
 */
const val TokenAuthChallengeKey: String = "BearerTokenAuth"
