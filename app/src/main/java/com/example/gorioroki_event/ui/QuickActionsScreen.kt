package com.example.gorioroki_event.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsScreen(
    navController: NavController,
    viewModel: EventViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // State untuk input
    var dateFrom by remember { mutableStateOf<String?>(null) }
    var dateTo by remember { mutableStateOf<String?>(null) }
    var eventIdInput by remember { mutableStateOf("") }
    var deleteIdInput by remember { mutableStateOf("") }

    // Dialog State
    var showStatsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Actions (Tester)") },
                navigationIcon = {
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- SECTION: Get Events ---
            SectionHeader("Get Events")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { 
                        viewModel.fetchAllEvents()
                        navController.navigate("event_list") // Kembali ke list untuk melihat hasil
                        Toast.makeText(context, "Fetching all events...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get All Events")
                }
                Button(
                    onClick = {
                        viewModel.fetchStatistics()
                        showStatsDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Statistics")
                }
            }

            // --- SECTION: Get by Date ---
            SectionHeader("Get by Date / Range")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField(label = "From Date (mm/dd/yyyy)", date = dateFrom) { dateFrom = it }
                DatePickerField(label = "To Date (mm/dd/yyyy)", date = dateTo) { dateTo = it }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (dateFrom != null) {
                                viewModel.fetchAllEvents(dateFrom, dateFrom) // Single date
                                navController.navigate("event_list")
                            } else {
                                Toast.makeText(context, "Select date first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Get by Date")
                    }
                    Button(
                        onClick = {
                            if (dateFrom != null && dateTo != null) {
                                viewModel.fetchAllEvents(dateFrom, dateTo)
                                navController.navigate("event_list")
                            } else {
                                Toast.makeText(context, "Select both dates", Toast.LENGTH_SHORT).show()
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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = eventIdInput,
                    onValueChange = { eventIdInput = it },
                    label = { Text("Event ID") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    if (eventIdInput.isNotBlank()) {
                        navController.navigate("event_detail/$eventIdInput")
                    }
                }) {
                    Text("Get Event")
                }
            }

            // --- SECTION: Update Event ---
            SectionHeader("Update Event")
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Di sini kita gunakan ID yang sama dengan "Get by ID" atau input baru
                // Untuk simplifikasi, tombol ini akan membawa ke Edit Screen
                Button(
                    onClick = {
                        if (eventIdInput.isNotBlank()) {
                            navController.navigate("edit_event/$eventIdInput")
                        } else {
                             Toast.makeText(context, "Enter Event ID above", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load for Update")
                }
            }

            // --- SECTION: Delete Event ---
            SectionHeader("Delete Event")
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = deleteIdInput,
                    onValueChange = { deleteIdInput = it },
                    label = { Text("Event ID") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (deleteIdInput.isNotBlank()) {
                            viewModel.deleteEvent(deleteIdInput) {
                                Toast.makeText(context, "Event Deleted!", Toast.LENGTH_SHORT).show()
                                deleteIdInput = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Event")
                }
            }
        }
    }

    // Dialog Statistik
    if (showStatsDialog && viewModel.statistics.value != null) {
        val stats = viewModel.statistics.value!!
        AlertDialog(
            onDismissRequest = { showStatsDialog = false },
            title = { Text("Event Statistics") },
            text = {
                Column {
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

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DatePickerField(label: String, date: String?, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            // Format YYYY-MM-DD sesuai API
            val selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedButton(
        onClick = { datePickerDialog.show() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = date ?: label)
    }
}
