package io.github.ackuq.configuration

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.Principal
import io.ktor.server.auth.jwt.jwt

fun handleAuthentication(uuid: String, roles: /* List<Role>? */ List<Any>?): Principal? {
    return null
    // TODO: Implement
/*    if (uuid == "") {
        return null
    }

    val user = UserService.getUserByUUID(UUID.fromString(uuid)) ?: return null

    if (roles != null) {
        if (user.role !in roles) {
            return null
        }
    }

    return user*/
}

fun Application.configureJWT() {
    install(Authentication) {
        jwt(SecurityConfigurations.Names.DEFAULT) {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, null)
            }
        }
        jwt(SecurityConfigurations.Names.ADMIN) {
            verifier(JwtConfig.verifier)
            validate {
                val uuid = it.payload.getClaim("uuid").asString()
                handleAuthentication(uuid, /* listOf(Role.Admin) */ null)
            }
        }
    }
}

object SecurityConfigurations {
    object Names {
        const val DEFAULT = "default"
        const val ADMIN = "admin"
    }
}
