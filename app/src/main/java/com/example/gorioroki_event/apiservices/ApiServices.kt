package com.example.gorioroki_event.apiservices

import com.example.gorioroki_event.models.ApiResponse
import com.example.gorioroki_event.models.Event
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

// --- 1. Interface Service untuk Retrofit ---
// Disesuaikan 100% dengan dokumentasi API terbaru
interface ApiService {

    // GET /api.php -> Get all events
    // GET /api.php?status=... -> Get events by status
    // GET /api.php?date_from=...&date_to=... -> Get events by date range
    // GET /api.php?date=... -> Get events by single date
    @GET("api.php")
    suspend fun getAllEvents(
        @Query("status") status: String? = null,
        @Query("date") date: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): Response<ApiResponse<List<Event>>>

    // GET /api.php?id=... -> Get event by ID
    @GET("api.php")
    suspend fun getEventById(
        @Query("id") id: String
    ): Response<ApiResponse<Event>>

    // POST /api.php -> Create new event
    @POST("api.php")
    suspend fun createEvent(
        @Body event: Event
    ): Response<ApiResponse<Event>>

    // PUT /api.php?id=... -> Update event
    @PUT("api.php")
    suspend fun updateEvent(
        @Query("id") id: String,
        @Body event: Event
    ): Response<ApiResponse<Event>>

    // DELETE /api.php?id=... -> Delete event
    @DELETE("api.php")
    suspend fun deleteEvent(
        @Query("id") id: String
    ): Response<ApiResponse<Unit>>

    // GET /api.php?stats=1 -> Get statistics
    @GET("api.php")
    suspend fun getStatistics(
        @Query("stats") stats: Int = 1
    ): Response<ApiResponse<Map<String, Any>>>
}

// --- 2. Objek Singleton untuk Membuat Instance Retrofit ---
object RetrofitInstance {

    // Base URL sesuai dokumentasi
    private const val BASE_URL = "http://104.248.153.158/event-api/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
