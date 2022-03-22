package io.github.ackuq.configuration

import io.github.ackuq.utils.handleApiError
import io.github.ackuq.utils.handleApiException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import kotlinx.serialization.SerializationException

inline fun <reified T : Throwable> StatusPagesConfig.exceptionHandle(
    status: HttpStatusCode,
    doThrow: Boolean = false
) =
    exception<T> { call, err ->
        handleApiException(err, status, call)
        if (doThrow) {
            throw err
        }
    }

fun Application.configureStatusPages() {
    install(StatusPages) {
        /**
         * Exceptions
         */
        // When the entity is not found
        exceptionHandle<NotFoundException>(HttpStatusCode.NotFound)
        // When the request is bad
        exceptionHandle<BadRequestException>(HttpStatusCode.BadRequest)
        // No or expired credentials
        exceptionHandle<AuthenticationException>(HttpStatusCode.Unauthorized)
        // When not enough permission
        exceptionHandle<AuthorizationException>(HttpStatusCode.Forbidden)
        // Serialization errors due to payload
        exceptionHandle<SerializationException>(HttpStatusCode.BadRequest)
        // Something unexpected
        exceptionHandle<Throwable>(
            HttpStatusCode.InternalServerError,
            doThrow = true
        )
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
