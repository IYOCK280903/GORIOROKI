package com.example.gorioroki_event.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gorioroki_event.models.Event
import com.example.gorioroki_event.viewmodels.EventViewModel
import java.util.Calendar
import java.util.Locale

// Palet Warna
private val DarkBackgroundStart = Color(0xFF111827)
private val DarkBackgroundEnd = Color(0xFF1F2937)
private val AccentBlue = Color(0xFF3B82F6)
private val GlassyColor = Color.White.copy(alpha = 0.05f)
private val GlassyBorder = Color.White.copy(alpha = 0.1f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    navController: NavController,
    eventId: String,
    viewModel: EventViewModel = viewModel()
) {
    // State variables
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("upcoming") }
    
    var expandedStatus by remember { mutableStateOf(false) }
    var expandedCapacity by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Load data
    LaunchedEffect(eventId) {
        viewModel.fetchEventById(eventId) { event ->
            title = event.title
            date = event.date
            time = event.time
            location = event.location
            description = event.description
            capacity = event.capacity.toString()
            status = event.status
        }
    }

    // Date Picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time Picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val scrollState = rememberScrollState()

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
                    title = { Text("Edit Event", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = GlassyColor),
                    border = BorderStroke(1.dp, GlassyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernTextField(value = title, onValueChange = { title = it }, label = "Title", icon = Icons.Default.Edit)
                        
                        // Date Field
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ModernTextField(value = date, onValueChange = {}, label = "Date", icon = Icons.Default.DateRange, readOnly = true)
                            Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
                        }

                        // Time Field
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ModernTextField(value = time, onValueChange = {}, label = "Time", icon = Icons.Default.Schedule, readOnly = true)
                            Box(modifier = Modifier.matchParentSize().clickable { timePickerDialog.show() })
                        }

                        ModernTextField(value = location, onValueChange = { location = it }, label = "Location", icon = Icons.Default.LocationOn)
                        ModernTextField(value = description, onValueChange = { description = it }, label = "Description", icon = Icons.Default.Info, minLines = 3)

                        // Capacity Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedCapacity,
                            onExpandedChange = { expandedCapacity = !expandedCapacity }
                        ) {
                            OutlinedTextField(
                                value = capacity,
                                onValueChange = { if (it.all { char -> char.isDigit() }) capacity = it },
                                label = { Text("Capacity", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AccentBlue) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCapacity) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                                    .fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = GlassyBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.LightGray,
                                    cursorColor = AccentBlue,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCapacity, 
                                onDismissRequest = { expandedCapacity = false }
                            ) {
                                listOf("5", "10", "20", "50", "100", "200").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) }, 
                                        onClick = { capacity = option; expandedCapacity = false }
                                    )
                                }
                            }
                        }

                        // Status Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedStatus,
                            onExpandedChange = { expandedStatus = !expandedStatus }
                        ) {
                             OutlinedTextField(
                                value = status.replaceFirstChar { it.uppercase() },
                                onValueChange = {},
                                label = { Text("Status", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentBlue) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = GlassyBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.LightGray,
                                    cursorColor = AccentBlue,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatus, 
                                onDismissRequest = { expandedStatus = false }
                            ) {
                                listOf("upcoming", "ongoing", "completed", "cancelled").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.replaceFirstChar { it.uppercase() }) }, 
                                        onClick = { status = option; expandedStatus = false }
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (title.isBlank() || date.isBlank() || time.isBlank()) {
                            Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                        } else {
                            val updatedEvent = Event(
                                id = eventId,
                                title = title,
                                date = date,
                                time = if (time.count { it == ':' } == 1) "$time:00" else time,
                                location = location,
                                description = description,
                                capacity = capacity.toIntOrNull() ?: 0,
                                status = status
                            )
                            viewModel.updateEvent(eventId, updatedEvent) {
                                Toast.makeText(context, "Event Updated!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    minLines: Int = 1,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = AccentBlue) },
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        minLines = minLines,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = GlassyBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.LightGray,
            cursorColor = AccentBlue,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        keyboardOptions = keyboardOptions
    )
}
