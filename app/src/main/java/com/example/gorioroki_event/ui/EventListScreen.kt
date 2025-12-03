package com.example.gorioroki_event.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun EventListScreen(
    navController: NavController,
    viewModel: EventViewModel = viewModel()
) {
    // Trigger fetch sekali saja saat screen pertama kali muncul
    LaunchedEffect(Unit) {
        viewModel.fetchAllEvents()
    }

    val events by viewModel.events
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.error

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Event") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_event") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Event Baru")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchAllEvents() }) {
                            Text("Coba Lagi")
                        }
                    }
                }

                events.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Belum ada event", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tekan tombol + untuk membuat event baru")
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(events, key = { it.id ?: 0 }) { event ->
                            EventItem(
                                event = event,
                                onClick = {
                                    event.id?.let { id ->
                                        navController.navigate("event_detail/$id")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${event.date} • ${event.time.ifBlank { "Waktu belum diatur" }}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.location,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChipStatus(status = event.status)
        }
    }
}

@Composable
private fun ChipStatus(status: String) {
    val color = when (status.lowercase()) {
        "upcoming" -> MaterialTheme.colorScheme.tertiaryContainer
        "ongoing" -> MaterialTheme.colorScheme.primaryContainer
        "completed" -> MaterialTheme.colorScheme.secondaryContainer
        "cancelled" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    AssistChip(
        onClick = { },
        label = { Text(status.replaceFirstChar { it.uppercase() }) },
        colors = AssistChipDefaults.assistChipColors(containerColor = color)
    )
}
