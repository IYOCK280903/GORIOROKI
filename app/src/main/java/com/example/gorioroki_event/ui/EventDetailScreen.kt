package com.example.gorioroki_event.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gorioroki_event.viewmodels.EventViewModel

// Palet Warna Tema Gelap Modern (Konsisten)
private val DarkBackgroundStart = Color(0xFF111827)
private val DarkBackgroundEnd = Color(0xFF1F2937)
private val AccentPink = Color(0xFFEC4899)
private val AccentBlue = Color(0xFF3B82F6)
private val GlassyColor = Color.White.copy(alpha = 0.05f)
private val GlassyBorder = Color.White.copy(alpha = 0.1f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavController,
    eventId: String,
    viewModel: EventViewModel = viewModel()
) {
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackgroundStart
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.background(
                brush = Brush.verticalGradient(listOf(DarkBackgroundStart, DarkBackgroundEnd))
            ),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Detail Event", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
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
                    CircularProgressIndicator(color = AccentPink)
                } else if (error != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Error fetching event with ID: $eventId",
                            color = AccentPink,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Details: $error",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchEventById(eventId) {} },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
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
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GlassyColor),
                            border = BorderStroke(1.dp, GlassyBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailItemModern(label = "ID", value = e.id ?: "N/A")
                                DetailItemModern(label = "Judul", value = e.title)
                                DetailItemModern(label = "Tanggal", value = e.date)
                                DetailItemModern(label = "Waktu", value = e.time.ifBlank { "-" })
                                DetailItemModern(label = "Lokasi", value = e.location)
                                DetailItemModern(label = "Deskripsi", value = e.description.ifBlank { "-" })
                                DetailItemModern(label = "Kapasitas", value = e.capacity.toString())
                                DetailItemModern(label = "Status", value = e.status.replaceFirstChar { it.uppercase() })
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = {
                                    e.id?.let { navController.navigate("edit_event/$it") }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                                border = BorderStroke(1.dp, AccentBlue)
                            ) {
                                Text("Edit")
                            }
                            Button(
                                onClick = {
                                    e.id?.let {
                                        viewModel.deleteEvent(it) {
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                            ) {
                                Text("Hapus")
                            }
                        }
                    }
                } else {
                    Text("Event not found or has been deleted.", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun DetailItemModern(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = AccentBlue
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = GlassyBorder)
    }
}
