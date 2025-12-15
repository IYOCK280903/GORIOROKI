package com.example.gorioroki_event.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gorioroki_event.viewmodels.EventViewModel
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: String,
    viewModel: EventViewModel = viewModel()
) {
    val context = LocalContext.current
    val event by viewModel.selectedEvent
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    DisposableEffect(Unit) {
        onDispose {
            viewModel.selectedEvent.value = null
            viewModel.error.value = null
        }
    }

    LaunchedEffect(eventId) {
        if (viewModel.selectedEvent.value?.id != eventId) {
            viewModel.fetchEventById(eventId) {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Error fetching event with ID: $eventId",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Details: $error",
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchEventById(eventId) {} }) {
                        Text("Try Again")
                    }
                }
            } else if (event != null) {
                val e = event!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    DetailItem(label = "ID", value = e.id ?: "N/A")
                    DetailItem(label = "Judul", value = e.title)
                    DetailItem(label = "Tanggal", value = e.date)
                    DetailItem(label = "Waktu", value = e.time.ifBlank { "-" })
                    DetailItem(label = "Lokasi", value = e.location)
                    DetailItem(label = "Deskripsi", value = e.description.ifBlank { "-" })
                    DetailItem(label = "Kapasitas", value = e.capacity.toString())
                    DetailItem(label = "Status", value = e.status.replaceFirstChar { it.uppercase() })

                    Spacer(modifier = Modifier.weight(1f))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                e.id?.let { navController.navigate("edit_event/$it") }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = {
                                e.id?.let {
                                    viewModel.deleteEvent(it) {
                                        Toast.makeText(context, "Event '${e.title}' deleted", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Hapus")
                        }
                    }
                }
            } else {
                Text("Event not found or has been deleted.")
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}
