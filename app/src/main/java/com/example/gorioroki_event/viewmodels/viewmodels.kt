// 1. PERBAIKAN: Package sudah benar (tanpa ada 'import' menempel)
package com.example.gorioroki_event.viewmodels

// 2. PERBAIKAN: Menghapus import yang duplikat dan merapikan
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gorioroki_event.ApiServices.RetrofitInstance
import com.example.gorioroki_event.models.Event
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    // State untuk menampung data dari API
    val events = mutableStateOf<List<Event>>(emptyList())
    val statistics = mutableStateOf<Map<String, String>?>(null)
    val error = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false) // Added isLoading state
    val selectedEvent = mutableStateOf<Event?>(null)

    fun fetchAllEvents() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Sekarang RetrofitInstance sudah dikenali
                val response = RetrofitInstance.api.getAllEvents()
                if (response.status == 200) {
                    events.value = response.data ?: emptyList()
                    error.value = null
                } else {
                    error.value = "Server Error: ${response.message}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchEventById(id: String, onSuccess: (Event) -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getEventById(id = id)
                if (response.status == 200) {
                    response.data?.let {
                        selectedEvent.value = it
                        onSuccess(it)
                    }
                } else {
                    error.value = response.message
                }
            } catch (e: Exception) {
                error.value = e.localizedMessage
            } finally {
                isLoading.value = false
            }
        }
    }

    fun createEvent(event: Event, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createEvent(event = event)
                if (response.status == 201 || response.status == 200) {
                    onSuccess()
                    fetchAllEvents()
                } else {
                    error.value = "Failed to create: ${response.message}"
                }
            } catch (e: Exception) {
                error.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateEvent(id: String, event: Event, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateEvent(id = id, event = event)
                if (response.status == 200) {
                    onSuccess()
                    fetchAllEvents()
                } else {
                    error.value = response.message
                }
            } catch (e: Exception) {
                error.value = e.localizedMessage
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteEvent(id: String, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteEvent(id = id)
                if (response.status == 200) {
                    onSuccess()
                    fetchAllEvents()
                } else {
                    error.value = response.message
                }
            } catch (e: Exception) {
                error.value = e.localizedMessage
            } finally {
                isLoading.value = false
            }
        }
    }
}
