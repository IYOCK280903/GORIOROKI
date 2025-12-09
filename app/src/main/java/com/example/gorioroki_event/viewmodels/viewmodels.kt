package com.example.gorioroki_event.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// PERBAIKAN: Import dari paket 'apiservices' (huruf kecil)
import com.example.gorioroki_event.apiservices.RetrofitInstance
import com.example.gorioroki_event.models.Event
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    // State
    val events = mutableStateOf<List<Event>>(emptyList())
    val statistics = mutableStateOf<Map<String, Any>?>(null)
    val error = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false)
    val selectedEvent = mutableStateOf<Event?>(null)

    // PERBAIKAN: Menggunakan response.isSuccessful dan response.body()
    fun fetchAllEvents() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Panggil fungsi dari ApiService
                val response = RetrofitInstance.api.getAllEvents()

                if (response.isSuccessful) {
                    // Jika berhasil, ambil datanya dari body
                    events.value = response.body() ?: emptyList()
                    error.value = null // Bersihkan error jika sukses
                } else {
                    // Jika gagal (misal: 404, 500), tampilkan kode error
                    error.value = "Error fetching events: ${response.code()}"
                }
            } catch (e: Exception) {
                // Jika terjadi error jaringan (misal: tidak ada internet)
                error.value = "Network Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // PERBAIKAN: Menggunakan response.isSuccessful dan response.body()
    fun fetchEventById(id: String, onSuccess: (Event) -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getEventById(id = id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        selectedEvent.value = it
                        onSuccess(it)
                    }
                } else {
                    error.value = "Error fetching event: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // PERBAIKAN: Menggunakan response.isSuccessful
    fun createEvent(event: Event, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createEvent(event = event)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchAllEvents() // Muat ulang data setelah berhasil
                } else {
                    error.value = "Failed to create: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // PERBAIKAN: Menggunakan response.isSuccessful
    fun updateEvent(id: String, event: Event, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateEvent(id = id, event = event)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchAllEvents()
                } else {
                    error.value = "Failed to update: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // PERBAIKAN: Menggunakan response.isSuccessful
    fun deleteEvent(id: String, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteEvent(id = id)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchAllEvents()
                } else {
                    error.value = "Failed to delete: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // PERBAIKAN: Menggunakan response.isSuccessful dan response.body()
    fun fetchStatistics() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getStatistics()
                if (response.isSuccessful) {
                    // Anda mungkin perlu mengubah Map<String, Any> menjadi Map<String, String>
                    // atau menangani konversi tipe data di sini.
                    val stats = response.body()?.mapValues { it.value.toString() }
                    statistics.value = stats
                } else {
                    error.value = "Failed to fetch statistics: ${response.code()}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
