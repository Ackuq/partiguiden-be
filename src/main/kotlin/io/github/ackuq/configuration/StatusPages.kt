package io.github.ackuq.configuration

import io.github.ackuq.utils.handleApiError
import io.github.ackuq.utils.handleApiException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.StatusPages
import kotlinx.serialization.SerializationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        /**
         * Exceptions
         */
        exception<Throwable> { call, cause ->
            when (cause) {
                is NotFoundException -> handleApiException(cause, HttpStatusCode.NotFound, call)
                is BadRequestException, is SerializationException -> handleApiException(
                    cause,
                    HttpStatusCode.BadRequest,
                    call
                )
                is AuthenticationException -> handleApiException(cause, HttpStatusCode.Unauthorized, call)
                is AuthorizationException -> handleApiException(cause, HttpStatusCode.Forbidden, call)
                else -> {
                    handleApiException(cause, HttpStatusCode.InternalServerError, call)
                    // Throw this error so we know what went wrong
                    throw cause
                }
            }
        }
        /**
         * Statuses
         */
        status(HttpStatusCode.NotFound) { call, status ->
            handleApiError("The resource was not found", status, call)
        }
        status(HttpStatusCode.BadRequest) { call, status ->
            handleApiError("Bad request", status, call)
        }
        status(HttpStatusCode.Conflict) { call, status ->
            handleApiError("Conflicting request", status, call)
        }
        status(HttpStatusCode.Forbidden) { call, status ->
            handleApiError("You do not have permission to access this", status, call)
        }
        status(HttpStatusCode.Unauthorized) { call, status ->
            handleApiError("Not authorized to view this page", status, call)
        }
    }
}

class AuthenticationException(message: String) : RuntimeException(message)
class AuthorizationException(message: String) : RuntimeException(message)
