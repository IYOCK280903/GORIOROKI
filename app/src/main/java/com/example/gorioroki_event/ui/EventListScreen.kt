package com.example.gorioroki_event.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gorioroki_event.models.Event
import com.example.gorioroki_event.viewmodels.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Palet Warna Tema Gelap Modern
private val DarkBackgroundStart = Color(0xFF111827) // Gray 900
private val DarkBackgroundEnd = Color(0xFF1F2937)  // Gray 800
private val AccentPink = Color(0xFFEC4899) // Cerah/Aksi
private val AccentBlue = Color(0xFF3B82F6) // Primer
private val GlassyColor = Color.White.copy(alpha = 0.05f) // Background Card Transparan
private val GlassyBorder = Color.White.copy(alpha = 0.1f)

@Composable
fun EventListScreen(
    navController: NavController,
    viewModel: EventViewModel = viewModel()
) {
    // Mengambil data saat layar pertama kali ditampilkan
    LaunchedEffect(Unit) {
        viewModel.fetchAllEvents()
        viewModel.fetchStatistics()
    }

    val isLoading by viewModel.isLoading
    val error by viewModel.error

    // Menggunakan state di ViewModel agar filter konsisten
    val filter by viewModel.currentFilter
    val statusCounts = viewModel.getStatusCounts()

    val filteredEvents = viewModel.getFilteredEvents()

    // Latar belakang utama dengan gradient
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackgroundStart
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("create_event") },
                    shape = CircleShape,
                    containerColor = AccentPink,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Event")
                }
            },
            containerColor = Color.Transparent,
            modifier = Modifier.background(
                brush = Brush.verticalGradient(listOf(DarkBackgroundStart, DarkBackgroundEnd))
            )
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header Kustom
                ModernHeader(
                    onRefreshClick = { 
                        viewModel.fetchAllEvents() 
                        viewModel.fetchStatistics()
                    },
                    onQuickActionsClick = { navController.navigate("quick_actions") }
                )

                // Tab Filter
                FilterTabs(
                    selectedFilter = filter,
                    counts = statusCounts,
                    onFilterSelected = { newFilter -> viewModel.setFilter(newFilter) }
                )

                // Konten Utama dengan Animasi
                AnimatedContent(
                    targetState = when {
                        isLoading -> "loading"
                        error != null -> "error"
                        filteredEvents.isEmpty() -> "empty"
                        else -> "content"
                    },
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f))
                            .togetherWith(fadeOut(animationSpec = tween(200)))
                    },
                    label = "Content"
                ) { state ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (state) {
                            "loading" -> CircularProgressIndicator(color = AccentPink)
                            "error" -> ErrorView(error = error, onRetry = { viewModel.fetchAllEvents() })
                            "empty" -> EmptyView(onCreateClick = { navController.navigate("create_event") })
                            "content" -> EventListContent(
                                events = filteredEvents,
                                onItemClick = { eventId ->
                                    navController.navigate("event_detail/$eventId")
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
private fun ModernHeader(onRefreshClick: () -> Unit, onQuickActionsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "GoRioRoki Events",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Test & Monitor Event Management",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Row {
            IconButton(onClick = onQuickActionsClick) {
                Icon(Icons.Default.Build, contentDescription = "Quick Actions", tint = Color.White)
            }
            IconButton(onClick = onRefreshClick) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
            }
        }
    }
}

@Composable
private fun FilterTabs(
    selectedFilter: String,
    counts: Map<String, Int>,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("all", "upcoming", "ongoing", "completed", "cancelled")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            Button(
                onClick = { onFilterSelected(filter) },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) AccentBlue else GlassyColor,
                    contentColor = if (isSelected) Color.White else Color.LightGray
                ),
                border = if (!isSelected) BorderStroke(1.dp, GlassyBorder) else null,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(filter.replaceFirstChar { it.uppercase() }, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                Text(
                    " ${counts[filter] ?: 0}",
                    fontSize = 10.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun EventListContent(events: List<Event>, onItemClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events, key = { it.id ?: "" }) { event ->
            ModernEventCard(event = event) {
                event.id?.let { onItemClick(it) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernEventCard(event: Event, onClick: () -> Unit) {
    val statusColor = when (event.status.lowercase()) {
        "upcoming" -> AccentBlue
        "ongoing" -> Color(0xFFF59E0B) // Amber
        "completed" -> Color(0xFF10B981) // Emerald
        "cancelled" -> AccentPink
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassyColor),
        border = BorderStroke(1.dp, GlassyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateBlockModern(dateString = event.date)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                InfoRowModern(icon = Icons.Default.LocationOn, text = event.location)
                Spacer(Modifier.height(4.dp))
                InfoRowModern(icon = Icons.Default.Schedule, text = if (event.time.length >= 5) event.time.take(5) + " WIB" else event.time)
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = event.status.replaceFirstChar { it.uppercase() },
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DateBlockModern(dateString: String) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val date = try { LocalDate.parse(dateString, formatter) } catch (_: Exception) { null }
    val day = date?.dayOfMonth?.toString() ?: "?"
    val month = date?.month?.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())?.uppercase(Locale.getDefault()) ?: "???"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AccentBlue.copy(alpha = 0.1f))
            .padding(vertical = 4.dp)
    ) {
        Text(day, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(month, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
    }
}

@Composable
private fun InfoRowModern(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
private fun ErrorView(error: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(Icons.Default.CloudOff, contentDescription = null, tint = AccentPink, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("Gagal Memuat Data", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Text(
            error ?: "Terjadi kesalahan yang tidak diketahui.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = AccentPink)) {
            Text("Coba Lagi")
        }
    }
}

@Composable
private fun EmptyView(onCreateClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            Icons.Default.EventBusy,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Tidak Ada Event Ditemukan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            "Sepertinya daftar event di filter ini kosong. Ayo buat event baru!",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Buat Event Baru")
        }
    }
}
