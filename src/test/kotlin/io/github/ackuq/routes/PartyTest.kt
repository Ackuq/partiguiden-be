package io.github.ackuq.routes

import io.github.ackuq.models.dto.NewPartyDTO
import io.github.ackuq.models.dto.PartyDTO
import io.github.ackuq.models.services.PartyService
import io.github.ackuq.routes.utils.ApiError
import io.github.ackuq.routes.utils.ApiSuccess
import io.github.ackuq.utils.TestExtension
import io.github.ackuq.utils.withTestServer
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals

class PartyTest : TestExtension() {
    private val testParty = NewPartyDTO(name = "Test Party", abbreviation = "T")

    @Test
    fun createParty() = withTestServer { client ->
        val response = client.post("/api/v1/parties") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer test")
            setBody(testParty)
        }
        val data = response.body<ApiSuccess<PartyDTO>>()
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(testParty.name, data.result.name)
        assertEquals(testParty.abbreviation, data.result.abbreviation)
    }

    @Test
    fun createPartyNoAuth() = withTestServer { client ->
        val response = client.post("/api/v1/parties") {
            contentType(ContentType.Application.Json)
            setBody(testParty)
        }
        val data = response.body<ApiError>()
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(HttpStatusCode.Unauthorized.value, data.status)
    }

    @Test
    fun listParties() = withTestServer { client ->
        PartyService.createParty(testParty)
        val response = client.get("/api/v1/parties")
        val data = response.body<ApiSuccess<List<PartyDTO>>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(1, data.result.size)
        val party = data.result[0]
        assertEquals(testParty.name, party.name)
        assertEquals(testParty.abbreviation, party.abbreviation)
    }

    @Test
    fun getParty() = withTestServer { client ->
        PartyService.createParty(testParty)
        val response = client.get("/api/v1/parties/${testParty.abbreviation}")
        val data = response.body<ApiSuccess<PartyDTO>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(testParty.name, data.result.name)
        assertEquals(testParty.abbreviation, data.result.abbreviation)
    }

    @Test
    fun getPartyNotFound() = withTestServer { client ->
        val response = client.get("/api/v1/parties/${testParty.abbreviation}")
        val data = response.body<ApiError>()

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(HttpStatusCode.NotFound.value, data.status)
    }
}
