package com.example.gorioroki_event.ApiServices

import com.example.gorioroki_event.models.Event
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// --- 1. Tambahkan Class ApiResponse di sini agar tidak error "Unresolved reference" ---
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)

// --- 2. Object Singleton Retrofit ---
object RetrofitInstance {
    // Pastikan port ini sesuai (3000 atau 8000)
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    // PERBAIKAN DISINI:
    // Jangan pakai 'com.google.firebase...', cukup pakai 'ApiService' saja.
    // Ini akan merujuk ke interface ApiService di bawah.
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// --- 3. Interface API ---
interface ApiService {
    @GET("events")
    suspend fun getAllEvents(): ApiResponse<List<Event>>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: String): ApiResponse<Event>

    @POST("events")
    suspend fun createEvent(@Body event: Event): ApiResponse<Event>

    @PUT("events/{id}")
    suspend fun updateEvent(@Path("id") id: String, @Body event: Event): ApiResponse<Event>

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): ApiResponse<Unit>
}
