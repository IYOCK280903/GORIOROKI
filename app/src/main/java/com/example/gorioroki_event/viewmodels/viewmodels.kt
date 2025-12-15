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
    
    // Filter State
    val currentFilter = mutableStateOf("all")

    // Fungsi untuk mengubah filter dan mengambil ulang data
    fun setFilter(filter: String) {
        currentFilter.value = filter
        // Ambil event berdasarkan status filter
        val statusParam = if (filter == "all") null else filter
        fetchAllEvents(status = statusParam)
    }

    // Mengembalikan list event (sudah difilter dari API)
    fun getFilteredEvents(): List<Event> {
        return events.value
    }

    // Mengambil jumlah event per status dari statistik
    fun getStatusCounts(): Map<String, Int> {
        val stats = statistics.value
        if (stats != null) {
            val getCount = { key: String ->
                (stats[key] as? Number)?.toInt() 
                ?: (stats[key] as? String)?.toIntOrNull() 
                ?: 0
            }
            
            // Coba ambil total, jika tidak ada hitung dari kategori lain
            var total = getCount("total")
            if (total == 0) total = getCount("all")
            
            val upcoming = getCount("upcoming")
            val ongoing = getCount("ongoing")
            val completed = getCount("completed")
            val cancelled = getCount("cancelled")
            
            // Jika total masih 0, jumlahkan manual (optional logic)
            if (total == 0) {
                 total = upcoming + ongoing + completed + cancelled
            }

            return mapOf(
                "all" to total,
                "upcoming" to upcoming,
                "ongoing" to ongoing,
                "completed" to completed,
                "cancelled" to cancelled
            )
        }

        // Fallback ke data lokal (kurang akurat jika data terfilter)
        val allEvents = events.value
        return mapOf(
            "all" to allEvents.size,
            "upcoming" to allEvents.count { it.status.lowercase() == "upcoming" },
            "ongoing" to allEvents.count { it.status.lowercase() == "ongoing" },
            "completed" to allEvents.count { it.status.lowercase() == "completed" },
            "cancelled" to allEvents.count { it.status.lowercase() == "cancelled" }
        )
    }

    // Disesuaikan dengan dokumentasi API terbaru
    fun fetchAllEvents(
        status: String? = null,
        date: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Jika status tidak diberikan manual, gunakan currentFilter
                val effectiveStatus = status ?: if (currentFilter.value != "all") currentFilter.value else null
                
                val response = RetrofitInstance.api.getAllEvents(effectiveStatus, date, dateFrom, dateTo)

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
                        fetchStatistics() // Refresh stats
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
                        fetchStatistics() // Refresh stats
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
                        fetchStatistics() // Refresh stats
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
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getStatistics()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.status == 200) {
                        statistics.value = apiResponse.data
                        error.value = null
                    } else {
                        // don't set error for stats failure to avoid blocking UI
                    }
                } else {
                    // error.value = "Failed to fetch statistics: ${response.code()}"
                }
            } catch (e: Exception) {
                // silently fail
            }
        }
    }
}
