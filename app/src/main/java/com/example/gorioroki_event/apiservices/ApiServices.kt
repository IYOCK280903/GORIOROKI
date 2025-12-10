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

// =========================================================================
// 1. INTERFACE SERVICE (DEFINISI ENDPOINT API)
//    Interface ini mendefinisikan semua pemanggilan API untuk Event Management System.
//    Metode Retrofit (GET, POST, PUT, DELETE) digunakan sesuai dengan desain RESTful API.
// =========================================================================

interface ApiService {

    /**
     * Mengambil daftar semua event.
     * Secara opsional mendukung filter berdasarkan rentang tanggal.
     * HTTP Method: GET
     * Endpoint: api.php
     * (Catatan: Fungsi ini tetap menggunakan Query 'date_from' dan 'date_to' seperti yang didefinisikan sebelumnya)
     */
    @GET("api.php")
    suspend fun getAllEvents(
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): Response<ApiResponse<List<Event>>>

    /**
     * Mengambil detail event spesifik berdasarkan ID.
     * HTTP Method: GET
     * Endpoint: api.php?id={id}
     */
    @GET("api.php")
    suspend fun getEventById(
        // ID Event yang dicari dikirim melalui Query Parameter
        @Query("id") id: String
    ): Response<ApiResponse<Event>>

    /**
     * Membuat event baru.
     * Data event dikirim melalui Body (JSON).
     * HTTP Method: POST
     * Endpoint: api.php
     */
    @POST("api.php")
    suspend fun createEvent(
        // Objek Event dikonversi menjadi JSON dan dikirim sebagai Body
        @Body event: Event
    ): Response<ApiResponse<Event>>

    /**
     * Memperbarui event yang sudah ada.
     * ID event diidentifikasi melalui Query Parameter.
     * Data update dikirim melalui Body (JSON).
     * HTTP Method: PUT
     * Endpoint: api.php?id={id}
     */
    @PUT("api.php")
    suspend fun updateEvent(
        // ID Event yang akan diupdate
        @Query("id") id: String,
        // Data Event yang diperbarui
        @Body event: Event
    ): Response<ApiResponse<Event>>

    /**
     * Menghapus event spesifik berdasarkan ID.
     * HTTP Method: DELETE
     * Endpoint: api.php?id={id}
     */
    @DELETE("api.php")
    suspend fun deleteEvent(
        // ID Event yang akan dihapus
        @Query("id") id: String
    ): Response<ApiResponse<Unit>>

    /**
     * Mengambil data statistik event.
     * Query Parameter 'stats=1' digunakan untuk memicu logika statistik di server.
     * HTTP Method: GET
     * Endpoint: api.php?stats=1
     */
    @GET("api.php")
    suspend fun getStatistics(
        // Parameter 'stats' dengan nilai default 1 (sesuai API)
        @Query("stats") stats: Int = 1
    ): Response<ApiResponse<Map<String, Any>>>
}


// =========================================================================
// 2. RETROFIT INSTANCE (SINGLETON OBJECT)
//    Objek ini berfungsi untuk menginisialisasi dan menyediakan instance Retrofit.
// =========================================================================

object RetrofitInstance {

    // BASE_URL mengarah ke server online yang tercantum dalam dokumentasi.
    // Pastikan URL ini benar dan selalu diakhiri dengan garis miring (/).
    // Contoh: "http://104.248.153.158/event-api/"
    private const val BASE_URL = "http://104.248.153.158/event-api/"

    /**
     * Instance ApiService yang dibuat secara lazy (saat pertama kali diakses).
     */
    val api: ApiService by lazy {
        Retrofit.Builder()
            // Menetapkan URL dasar
            .baseUrl(BASE_URL)
            // Menambahkan konverter JSON (Gson)
            .addConverterFactory(GsonConverterFactory.create())
            // Membuat instance Retrofit
            .build()
            // Membuat implementasi dari ApiService
            .create(ApiService::class.java)
    }
}