package com.example.gorioroki_event.models

data class Event(
    val id: String? = null, // ID bisa null saat create baru
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val capacity: Int,
    val status: String
)
