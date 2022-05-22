package io.github.ackuq.utils

import io.github.ackuq.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication

fun withTestServer(block: suspend (HttpClient) -> Unit) = testApplication {
    environment {
        config = ApplicationConfig("application-test.conf")
    }

    application {
        module()
    }

    val httpClient = createClient {
        install(ContentNegotiation) {
            json()
        }
    }

    block(httpClient)
}
