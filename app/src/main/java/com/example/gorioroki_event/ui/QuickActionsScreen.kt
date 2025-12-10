package com.example.gorioroki_event.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gorioroki_event.viewmodels.EventViewModel
import java.util.Calendar
import java.util.Locale

// =========================================================================
// QuickActionsScreen
// Halaman utama untuk pengujian cepat (Quick Actions) endpoint API.
// Menggantikan peran Web API Tester untuk debugging/pengujian di mobile.
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsScreen(
    navController: NavController,
    // Menggunakan EventViewModel untuk menjalankan fungsi API
    viewModel: EventViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk input tanggal filter
    var dateFrom by remember { mutableStateOf<String?>(null) }
    var dateTo by remember { mutableStateOf<String?>(null) }
    // State untuk input ID event (digunakan untuk Get by ID dan Update)
    var eventIdInput by remember { mutableStateOf("") }
    // State untuk input ID event (khusus untuk Delete)
    var deleteIdInput by remember { mutableStateOf("") }

    // State untuk mengontrol tampil/tidaknya dialog statistik
    var showStatsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Actions (Tester)") },
                navigationIcon = {
                    // Tombol kembali ke halaman sebelumnya
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                // Memungkinkan scrolling untuk konten yang panjang
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- SECTION: Get Events & Statistics ---
            SectionHeader("Get Events")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tombol Get All Events
                Button(
                    onClick = {
                        // Memanggil ViewModel untuk mengambil semua data
                        viewModel.fetchAllEvents()
                        // Navigasi ke halaman list untuk menampilkan hasil
                        navController.navigate("event_list")
                        Toast.makeText(context, "Fetching all events...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get All Events")
                }
                // Tombol Get Statistics
                Button(
                    onClick = {
                        // Memanggil ViewModel untuk mengambil statistik
                        viewModel.fetchStatistics()
                        // Menampilkan dialog statistik
                        showStatsDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Statistics")
                }
            }

            // --- SECTION: Get by Date / Range ---
            SectionHeader("Get by Date / Range")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Field untuk memilih tanggal awal (From Date)
                DatePickerField(label = "From Date (mm/dd/yyyy)", date = dateFrom) { dateFrom = it }
                // Field untuk memilih tanggal akhir (To Date)
                DatePickerField(label = "To Date (mm/dd/yyyy)", date = dateTo) { dateTo = it }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tombol Get by Date (Single Date)
                    Button(
                        onClick = {
                            if (dateFrom != null) {
                                // Menggunakan dateFrom sebagai tanggal awal dan akhir untuk filter satu hari
                                viewModel.fetchAllEvents(dateFrom, dateFrom)
                                navController.navigate("event_list")
                            } else {
                                Toast.makeText(context, "Select date first", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Get by Date")
                    }
                    // Tombol Get by Range (Date Range)
                    Button(
                        onClick = {
                            if (dateFrom != null && dateTo != null) {
                                // Mengambil data berdasarkan rentang tanggal
                                viewModel.fetchAllEvents(dateFrom, dateTo)
                                navController.navigate("event_list")
                            } else {
                                Toast.makeText(context, "Select both dates", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Get by Range")
                    }
                }
            }

            // --- SECTION: Get by ID ---
            SectionHeader("Get by ID")
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Input field untuk ID Event
                OutlinedTextField(
                    value = eventIdInput,
                    onValueChange = { eventIdInput = it },
                    label = { Text("Event ID") },
                    modifier = Modifier.weight(1f)
                )
                // Tombol untuk navigasi ke Detail Event
                Button(onClick = {
                    if (eventIdInput.isNotBlank()) {
                        // Navigasi ke halaman detail dengan membawa ID sebagai argumen
                        navController.navigate("event_detail/$eventIdInput")
                    }
                }) {
                    Text("Get Event")
                }
            }

            // --- SECTION: Update Event ---
            SectionHeader("Update Event")
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tombol untuk Load data ke Edit Screen
                // Menggunakan eventIdInput yang sudah diisi di bagian Get by ID
                Button(
                    onClick = {
                        if (eventIdInput.isNotBlank()) {
                            // Navigasi ke halaman edit dengan membawa ID
                            navController.navigate("edit_event/$eventIdInput")
                        } else {
                            Toast.makeText(context, "Enter Event ID above", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load for Update")
                }
            }

            // --- SECTION: Delete Event ---
            SectionHeader("Delete Event")
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Input field khusus untuk ID Event yang akan dihapus
                OutlinedTextField(
                    value = deleteIdInput,
                    onValueChange = { deleteIdInput = it },
                    label = { Text("Event ID") },
                    modifier = Modifier.weight(1f)
                )
                // Tombol Delete
                Button(
                    onClick = {
                        if (deleteIdInput.isNotBlank()) {
                            // Memanggil fungsi delete di ViewModel
                            viewModel.deleteEvent(deleteIdInput) {
                                Toast.makeText(context, "Event Deleted!", Toast.LENGTH_SHORT).show()
                                // Reset input setelah berhasil
                                deleteIdInput = ""
                            }
                        }
                    },
                    // Menggunakan warna error (merah) untuk visualisasi operasi Delete
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Event")
                }
            }
        }
    }

    // --- Dialog Statistik ---
    // Menampilkan dialog jika showStatsDialog true dan data statistik sudah ada
    if (showStatsDialog && viewModel.statistics.value != null) {
        val stats = viewModel.statistics.value!!
        AlertDialog(
            onDismissRequest = { showStatsDialog = false },
            title = { Text("Event Statistics") },
            text = {
                Column {
                    // Menampilkan data statistik dari ViewModel
                    Text("Total: ${stats["total"] ?: 0}")
                    Text("Upcoming: ${stats["upcoming"] ?: 0}")
                    Text("Ongoing: ${stats["ongoing"] ?: 0}")
                    Text("Completed: ${stats["completed"] ?: 0}")
                    Text("Cancelled: ${stats["cancelled"] ?: 0}")
                }
            },
            confirmButton = {
                Button(onClick = { showStatsDialog = false }) { Text("Close") }
            }
        )
    }
}

// =========================================================================
// Helper Composables
// =========================================================================

/**
 * Komponen untuk menampilkan header setiap bagian (section).
 */
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

/**
 * Komponen tombol yang menampilkan DatePickerDialog saat diklik.
 * Memastikan format tanggal yang dipilih adalah YYYY-MM-DD (standar API).
 */
@Composable
fun DatePickerField(label: String, date: String?, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Inisialisasi DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            // Format output tanggal ke YYYY-MM-DD sesuai kebutuhan API
            val selectedDate =
                String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Tombol Outlined untuk memicu dialog
    OutlinedButton(
        onClick = { datePickerDialog.show() },
        modifier = Modifier.fillMaxWidth()
    ) {
        // Menampilkan tanggal yang sudah dipilih atau label default
        Text(text = date ?: label)
    }
}