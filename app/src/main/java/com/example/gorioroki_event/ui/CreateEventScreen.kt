package com.example.gorioroki_event.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gorioroki_event.models.Event







import com.example.gorioroki_event.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navController: NavController,
    viewModel: EventViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("upcoming") }
    var expanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time (HH:MM)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            minLines = 3
        )

        OutlinedTextField(
            value = capacity,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    capacity = input
                }
            },
            label = { Text("Capacity (number)") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Dropdown Status
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = status.replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor() // API Stabil untuk Material3 1.2.x
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("upcoming", "ongoing", "completed", "cancelled").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            status = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (title.isBlank() || date.isBlank() || capacity.isBlank()) {
                    return@Button
                }

                val event = Event(
                    // id = null, // ID diabaikan atau null saat create
                    title = title,
                    date = date,
                    time = time,
                    location = location,
                    description = description,
                    capacity = capacity.toIntOrNull() ?: 0,
                    status = status
                )

                viewModel.createEvent(event) {
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Text("Buat Event")
        }
    }
}
