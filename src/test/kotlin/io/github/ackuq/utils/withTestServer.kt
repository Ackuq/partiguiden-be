package io.github.ackuq.utils

import io.github.ackuq.module
import io.ktor.server.testing.testApplication

fun withTestServer() = testApplication {
    application {
        module(TestDatabaseFactory())
    }
}
