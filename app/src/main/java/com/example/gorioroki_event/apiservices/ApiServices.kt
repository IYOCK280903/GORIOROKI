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
// Disesuaikan dengan api.php yang Anda berikan
interface ApiService {

    // GET: Ambil semua data (api.php), bisa difilter by tanggal (opsional)
    // Mendukung "Get All Events", "Get by Date", dan "Get by Date Range"
    @GET("api.php")
    suspend fun getAllEvents(
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): Response<ApiResponse<List<Event>>>

    // GET: Ambil satu data (api.php?id=...)
    // Mendukung "Get by ID" dan "Load for Update"
    @GET("api.php")
    suspend fun getEventById(
        @Query("id") id: String
    ): Response<ApiResponse<Event>>

    // POST: Buat data baru (api.php, body JSON)
    @POST("api.php")
    suspend fun createEvent(
        @Body event: Event
    ): Response<ApiResponse<Event>>

    // PUT: Update data (api.php?id=..., body JSON)
    // api.php mengharuskan ID dikirim lewat Query Param untuk PUT
    @PUT("api.php")
    suspend fun updateEvent(
        @Query("id") id: String,
        @Body event: Event
    ): Response<ApiResponse<Event>>

    // DELETE: Hapus data (api.php?id=...)
    @DELETE("api.php")
    suspend fun deleteEvent(
        @Query("id") id: String
    ): Response<ApiResponse<Unit>>

    // GET: Statistik (api.php?stats=1)
    // Mendukung "Get Statistics"
    @GET("api.php")
    suspend fun getStatistics(
        @Query("stats") stats: Int = 1
    ): Response<ApiResponse<Map<String, Any>>>
}

// --- 2. Objek Singleton untuk Membuat Instance Retrofit ---
object RetrofitInstance {

    // Menggunakan alamat IP server online dari Web Tester
    private const val BASE_URL = "http://104.248.153.158/event-api/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
