package com.example.gorioroki_event.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gorioroki_event.models.Event
import com.example.gorioroki_event.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: String?,  // Bisa null dari NavArg
    viewModel: EventViewModel = viewModel()
) {
    // State yang lebih jelas
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch data saat screen muncul atau eventId berubah
    LaunchedEffect(eventId) {
        if (eventId.isNullOrBlank()) {
            errorMessage = "Event tidak ditemukan"
            isLoading = false
            return@LaunchedEffect
        }

        viewModel.fetchEventById(eventId) { fetchedEvent ->
            event = fetchedEvent
            isLoading = false
            if (fetchedEvent == null) {
                errorMessage = "Event tidak ditemukan atau gagal dimuat"
            }
        }
    }

    // UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Kembali")
                        }
                    }
                }

                event != null -> {
                    val e = event!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        DetailItem(label = "Judul", value = e.title)
                        DetailItem(label = "Tanggal", value = e.date)
                        DetailItem(label = "Waktu", value = e.time.ifBlank { "Tidak diatur" })
                        DetailItem(label = "Lokasi", value = e.location)
                        DetailItem(label = "Deskripsi", value = e.description)
                        DetailItem(label = "Kapasitas", value = e.capacity.toString())
                        DetailItem(label = "Status", value = e.status.replaceFirstChar { it.uppercase() })

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    // Pastikan id tidak null sebelum navigasi
                                    e.id?.let { id ->
                                        navController.navigate("edit_event/$id")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Edit")
                            }

                            OutlinedButton(
                                onClick = {
                                    e.id?.let { id ->
                                        viewModel.deleteEvent(id) {
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Hapus")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
