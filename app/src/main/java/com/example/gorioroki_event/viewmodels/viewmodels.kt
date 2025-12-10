package com.example.gorioroki_event.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // PERBAIKAN: Menambahkan parameter opsional untuk filter tanggal
    fun fetchAllEvents(dateFrom: String? = null, dateTo: String? = null) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Mengirim parameter filter ke API
                val response = RetrofitInstance.api.getAllEvents(dateFrom, dateTo)

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status == 200) {
                        events.value = apiResponse.data ?: emptyList()
                        error.value = null
                    } else {
                         error.value = apiResponse?.message ?: "Unknown API error"
                    }
                } else {
                    error.value = "Error fetching events: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
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
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                     if (apiResponse != null && apiResponse.status == 200) {
                        apiResponse.data?.let {
                            selectedEvent.value = it
                            onSuccess(it)
                            error.value = null
                        }
                    } else {
                        error.value = apiResponse?.message ?: "Error fetching event"
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

    fun createEvent(event: Event, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createEvent(event = event)
                if (response.isSuccessful) {
                     val apiResponse = response.body()
                     if (apiResponse != null && (apiResponse.status == 200 || apiResponse.status == 201)) {
                        onSuccess()
                        fetchAllEvents() // Refresh list
                        error.value = null
                    } else {
                        error.value = apiResponse?.message ?: "Failed to create event"
                    }
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

    fun updateEvent(id: String, event: Event, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateEvent(id = id, event = event)
                 if (response.isSuccessful) {
                     val apiResponse = response.body()
                     if (apiResponse != null && apiResponse.status == 200) {
                        onSuccess()
                        fetchAllEvents()
                        error.value = null
                    } else {
                        error.value = apiResponse?.message ?: "Failed to update event"
                    }
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

    fun deleteEvent(id: String, onSuccess: () -> Unit) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteEvent(id = id)
                 if (response.isSuccessful) {
                     val apiResponse = response.body()
                     if (apiResponse != null && apiResponse.status == 200) {
                        onSuccess()
                        fetchAllEvents()
                        error.value = null
                    } else {
                        error.value = apiResponse?.message ?: "Failed to delete event"
                    }
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

    fun fetchStatistics() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getStatistics()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status == 200) {
                        statistics.value = apiResponse.data
                        error.value = null
                    } else {
                        error.value = apiResponse?.message ?: "Failed to fetch statistics"
                    }
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
