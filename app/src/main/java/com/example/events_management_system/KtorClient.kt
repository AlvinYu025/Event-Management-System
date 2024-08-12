package com.example.events_management_system

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.engine.cio.*

@Serializable
data class Event(
    val _id: String,
    val title: String,
    val organiser: String,
    val description: String,
    val event_date: String,
    val location: String,
    val image: String,
    val quota: Int,
    val highlight: Boolean,
    val createdAt: String,
    val modifiedAt: String,
    val volunteers: List<String> = emptyList()
)

@Serializable
data class Volunteer(
    val email: String,
    val password: String,
    val name: String,
    val contact: String,
    val ageGroup: String,
    val about: String,
    val terms: Boolean
)

@Serializable
data class UserInfo(
    val _id: String,
    val email: String,
    val name: String,
    val contact: String,
    val age_group: String,
    val about: String,
    val terms: Boolean,
    val createdAt: String,
    val modifiedAt: String,
    val isAdmin: Boolean,
    val events: List<String> = emptyList()
)

object KtorClient {
    private var token: String = ""

    var httpClient = HttpClient {
        install(ContentNegotiation) {
            json() // enable the client to perform JSON serialization
        }
        install(Logging)
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        expectSuccess = true
    }

    @Serializable
    data class EventsResponse(
        val events: List<Event> = emptyList(),
        val total: Int,
        val perPage: Int,
        val page: Int
    )

    @Serializable
    data class UserInfoResponse(
        val events: List<Event> = emptyList(),
        val total: Int
    )

    @Serializable
    data class TokenResponse(
        val token: String
    )

    suspend fun getEvents(page: Int): List<Event> {
        var list: List<Event>
        try {
            val response: EventsResponse =
                httpClient.get("https://comp4107-spring2024.azurewebsites.net/api/events/?page=$page")
                    .body()
            list = response.events

        } catch (e: Exception) {
            e.printStackTrace()
            list = emptyList()
        }
        return list
    }

    suspend fun getEventsByLocation(page: Int, location: Int): List<Event> {
        var list: List<Event>
        try {
            val response: EventsResponse =
                httpClient.get("https://comp4107-spring2024.azurewebsites.net/api/events/?page=$page&location=${location}")
                    .body()
            list = response.events

        } catch (e: Exception) {
            e.printStackTrace()
            list = emptyList()
        }
        return list
    }

    suspend fun getEventById(_id: String): Event {
        var list: Event
        try {
            val response: Event =
                httpClient.get("https://comp4107-spring2024.azurewebsites.net/api/events/$_id")
                    .body()
            list = response
        } catch (e: Exception) {
            e.printStackTrace()
            list = Event("", "", "", "", "", "", "", 0, false, "", "")
        }
        return list
    }

    suspend fun getRegisteredEvents(): List<String> {
        var Ids: List<String>
        try {
            val response: UserInfo =
                httpClient.get("https://comp4107-spring2024.azurewebsites.net/api/volunteers/dummy")
                    .body()
            Ids = response.events

        } catch (e: Exception) {
            e.printStackTrace()
            Ids = emptyList()
        }
        return Ids
    }

    suspend fun getEventBySearch(search: String, page: Int): List<Event> {
        var list: List<Event>
        try {
            val response: EventsResponse =
                httpClient.get("https://comp4107-spring2024.azurewebsites.net/api/events/?page=$page&search=$search")
                    .body()
            list = response.events

        } catch (e: Exception) {
            e.printStackTrace()
            list = emptyList()
        }
        return list
    }

    suspend fun getUserEvents(page: Int): List<Event> {
        var list: List<Event>
        try {
            val response: UserInfoResponse =
                httpClient.get("https://comp4107-spring2024.azurewebsites.net/api/volunteers/dummy/events?page=$page")
                    .body()
            list = response.events

        } catch (e: Exception) {
            e.printStackTrace()
            list = emptyList()
        }
        return list
    }

    suspend fun volunteerRegistration(information: Volunteer) {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
            }
            expectSuccess = false
        }

        try {
            val response: HttpResponse =
                httpClient.post("https://comp4107-spring2024.azurewebsites.net/api/volunteers/") {
                    contentType(ContentType.Application.Json)
                    setBody(information)
                }

            if (response.status == HttpStatusCode.Created) {
                println("Registration succeed!")
            }
        } catch (e: Exception) {
            println("Registration failed: ${e.message}")
        } finally {
            httpClient.close()
        }
    }

    suspend fun JoinEvent(_id: String): Boolean {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
            }
            expectSuccess = false
        }
        return try {
            val response: HttpResponse =
                httpClient.post("https://comp4107-spring2024.azurewebsites.net/api/events/$_id/volunteers") {
                }

            if (response.status == HttpStatusCode.OK) {
                println("Event registered successfully.")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Join Event failed: ${e.message}")
            false
        } finally {
            httpClient.close()
        }
    }

    suspend fun DeleteEvent(_id: String): Boolean {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
            }
            expectSuccess = false
        }

        return try {
            val response: HttpResponse =
                httpClient.delete("https://comp4107-spring2024.azurewebsites.net/api/events/$_id/volunteers") {
                }

            if (response.status == HttpStatusCode.OK) {
                println("Event unregistered successfully.")
                println("Response status: ${response.status}")
                println("Response body: ${response.bodyAsText()}")

                true
            } else {
                print("yff")
                println("Response status: ${response.status}")
                println("Response body: ${response.bodyAsText()}")

                false
            }
        } catch (e: Exception) {
            println("Delete Event failed: ${e.message}")
            false
        } finally {
            httpClient.close()
        }
    }


    suspend fun getLoginToken(email: String, password: String): Boolean {
        val httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                if (token.isNotEmpty()) {
                    header("Authorization", "Bearer $token")
                }
            }
            expectSuccess = false
        }
        return try {
            val response: HttpResponse =
                httpClient.post("https://comp4107-spring2024.azurewebsites.net/api/login/") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"email": "$email", "password": "$password"}""")
                }
            if (response.status == HttpStatusCode.OK) {
                val jsonResponse = response.bodyAsText()
                val tokenResponse = Json.decodeFromString<TokenResponse>(jsonResponse)
                token = tokenResponse.token
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Login failed: ${e.message}")
            false
        } finally {
            httpClient.close()
        }
    }

    fun clearToken(){
        this.token = ""
    }
}
