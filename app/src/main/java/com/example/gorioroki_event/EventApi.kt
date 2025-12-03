package com.example.gorioroki_event.api

import com.example.gorioroki_event.models.Event
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url

// Wrapper class for API response
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)

interface EventApi {
    @GET
    suspend fun getAllEvents(@Url url: String = DEFAULT_ENDPOINT): ApiResponse<List<Event>>

    @GET
    suspend fun getEventById(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_ID) id: String): ApiResponse<Event>

    @GET
    suspend fun getEventsByDate(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_DATE) date: String): ApiResponse<List<Event>>

    @GET
    suspend fun getEventsByDateRange(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_DATE_FROM) from: String, @Query(PARAM_DATE_TO) to: String): ApiResponse<List<Event>>

    @GET
    suspend fun getEventsByStatus(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_STATUS) status: String): ApiResponse<List<Event>>

    @GET
    suspend fun getStatistics(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_STATS) stats: Int = 1): ApiResponse<Map<String, String>>

    @POST
    suspend fun createEvent(@Url url: String = DEFAULT_ENDPOINT, @Body event: Event): ApiResponse<Event>

    @PUT
    suspend fun updateEvent(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_ID) id: String, @Body event: Event): ApiResponse<Event>

    @DELETE
    suspend fun deleteEvent(@Url url: String = DEFAULT_ENDPOINT, @Query(PARAM_ID) id: String): ApiResponse<Any>

    companion object {
        const val DEFAULT_ENDPOINT = "api.php"
        const val PARAM_ID = "id"
        const val PARAM_DATE = "date"
        const val PARAM_DATE_FROM = "date_from"
        const val PARAM_DATE_TO = "date_to"
        const val PARAM_STATUS = "status"
        const val PARAM_STATS = "stats"
    }
}
