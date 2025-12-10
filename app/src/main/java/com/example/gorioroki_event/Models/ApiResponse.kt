package com.example.gorioroki_event.models

import com.google.gson.annotations.SerializedName

// Wrapper untuk mencocokkan format respons dari api.php
// Format JSON: { "status": 200, "message": "...", "data": ... }
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)
