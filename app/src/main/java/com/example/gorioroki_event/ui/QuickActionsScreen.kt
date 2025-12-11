package com.example.gorioroki_event.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var showStatsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Actions (Tester)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECTION: Get All & Statistics ---
            ActionCard("General Actions") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.fetchAllEvents()
                            navController.navigate("event_list")
                            Toast.makeText(context, "Fetching all events...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Get All Events") }

                    Button(
                        onClick = {
                            viewModel.fetchStatistics()
                            showStatsDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Get Statistics") }
                }
            }

            // --- SECTION: Filter Events ---
            var status by remember { mutableStateOf<String?>(null) }
            var date by remember { mutableStateOf<String?>(null) }
            var dateFrom by remember { mutableStateOf<String?>(null) }
            var dateTo by remember { mutableStateOf<String?>(null) }
            ActionCard("Filter Events") {
                // Filter by Status
                StatusFilterDropdown { status = it }
                Spacer(Modifier.height(8.dp))

                // Filter by Single Date
                DatePickerField(label = "Filter by Date", date = date) { date = it }
                Button(onClick = {
                    viewModel.fetchAllEvents(status = status, date = date)
                    navController.navigate("event_list")
                }, modifier = Modifier.fillMaxWidth()) { Text("Apply Filter") }

                HorizontalDivider(Modifier.padding(vertical = 16.dp))

                // Filter by Date Range
                DatePickerField(label = "From Date", date = dateFrom) { dateFrom = it }
                DatePickerField(label = "To Date", date = dateTo) { dateTo = it }
                Button(onClick = {
                    viewModel.fetchAllEvents(status = status, dateFrom = dateFrom, dateTo = dateTo)
                    navController.navigate("event_list")
                }, modifier = Modifier.fillMaxWidth()) { Text("Apply Range Filter") }
            }

            // --- SECTION: Actions by ID ---
            var eventIdInput by remember { mutableStateOf("") }
            ActionCard("Actions by ID") {
                OutlinedTextField(
                    value = eventIdInput,
                    onValueChange = { eventIdInput = it },
                    label = { Text("Enter Event ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { if (eventIdInput.isNotBlank()) navController.navigate("event_detail/$eventIdInput") }, modifier = Modifier.weight(1f)) { Text("Get") }
                    Button(onClick = { if (eventIdInput.isNotBlank()) navController.navigate("edit_event/$eventIdInput") }, modifier = Modifier.weight(1f)) { Text("Update") }
                    Button(
                        onClick = {
                            if (eventIdInput.isNotBlank()) {
                                viewModel.deleteEvent(eventIdInput) {
                                    Toast.makeText(context, "Event #$eventIdInput Deleted!", Toast.LENGTH_SHORT).show()
                                    eventIdInput = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) { Text("Delete") }
                }
            }
        }
    }

    if (showStatsDialog && viewModel.statistics.value != null) {
        StatisticsDialog(statistics = viewModel.statistics.value!!, onDismiss = { showStatsDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilterDropdown(onStatusSelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf<String?>("All Status") }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedStatus ?: "All Status",
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter by Status") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val statuses = listOf("All Status", "upcoming", "ongoing", "completed", "cancelled")
            statuses.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        selectedStatus = option
                        onStatusSelected(if (option == "All Status") null else option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ActionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}

@Composable
fun DatePickerField(label: String, date: String?, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedButton(
        onClick = { datePickerDialog.show() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = date?.let { "$label: $it" } ?: label)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatisticsDialog(statistics: Map<String, Any>, onDismiss: () -> Unit) {
    val total = (statistics["total"] as? Number)?.toInt() ?: 0
    val upcoming = (statistics["upcoming"] as? Number)?.toInt() ?: 0
    val ongoing = (statistics["ongoing"] as? Number)?.toInt() ?: 0
    val completed = (statistics["completed"] as? Number)?.toInt() ?: 0
    val cancelled = (statistics["cancelled"] as? Number)?.toInt() ?: 0

    val statItems = listOf(
        StatInfo("Total", total, Icons.Default.Summarize, MaterialTheme.colorScheme.primary),
        StatInfo("Upcoming", upcoming, Icons.Default.Upcoming, MaterialTheme.colorScheme.secondary),
        StatInfo("Ongoing", ongoing, Icons.Default.HourglassTop, MaterialTheme.colorScheme.tertiary),
        StatInfo("Completed", completed, Icons.Default.CheckCircle, Color(0xFF388E3C)),
        StatInfo("Cancelled", cancelled, Icons.Default.Cancel, MaterialTheme.colorScheme.error)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Event Statistics") },
        text = {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2
            ) {
                statItems.forEach { stat ->
                    StatCard(stat = stat, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

data class StatInfo(val label: String, val count: Int, val icon: ImageVector, val color: Color)

@Composable
fun StatCard(stat: StatInfo, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = stat.color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(stat.icon, contentDescription = null, tint = stat.color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = stat.count.toString(), 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = stat.color
                )
                Text(text = stat.label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
