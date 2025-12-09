// PERBAIKAN: Pastikan nama paket ini cocok dengan nama folder (semua huruf kecil)
package com.example.gorioroki_event.apiservices

import com.example.gorioroki_event.models.Event
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- 1. Interface yang mendefinisikan semua endpoint API ---
interface ApiService {

    @GET("api.php")
    suspend fun getAllEvents(@Query("action") action: String = "get_all"): Response<List<Event>>

    @GET("api.php")
    suspend fun getEventById(
        @Query("action") action: String = "get_by_id",
        @Query("id") id: String
    ): Response<Event>

    @POST("api.php")
    suspend fun createEvent(
        @Query("action") action: String = "create",
        @Body event: Event
    ): Response<Event>

    @POST("api.php")
    suspend fun updateEvent(
        @Query("action") action: String = "update",
        @Query("id") id: String,
        @Body event: Event
    ): Response<Event>

    @GET("api.php")
    suspend fun deleteEvent(
        @Query("action") action: String = "delete",
        @Query("id") id: String
    ): Response<Unit>

    @GET("api.php")
    suspend fun getStatistics(@Query("action") action: String = "get_statistics"): Response<Map<String, Any>>
}

// --- 2. Object yang membuat instance Retrofit (hanya sekali) ---
object RetrofitInstance {

    // BASE_URL disesuaikan dengan Event API Tester (biasanya port 80 atau tidak ada port untuk XAMPP)
    // Untuk Android Emulator, localhost komputer diakses via 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2/" // <--- INI UNTUK XAMPP BIASA

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
