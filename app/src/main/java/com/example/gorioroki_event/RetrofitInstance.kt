package com.example.gorioroki_event.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Ganti URL ini dengan URL backend Anda.
    // Jika pakai Emulator Android dan backend di localhost laptop, gunakan 10.0.2.2
    // Ganti 8000 menjadi 3000
    private const val BASE_URL = "http://10.0.2.2:3000/api/"


    val api: EventApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventApi::class.java)
    }
}
