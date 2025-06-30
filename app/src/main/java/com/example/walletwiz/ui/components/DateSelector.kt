package com.example.walletwiz.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import java.util.Date
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    onDateSelected: (Date) -> Unit
) {
    val now = Instant.now()
    var selectedDate by remember { mutableStateOf(Date.from(now)) }
    val today = Date.from(now)
    val yesterday = Date.from(now.minus(1, ChronoUnit.DAYS))

    val formatter = remember {
        DateTimeFormatter.ofPattern("dd. MM. yyyy").withZone(ZoneId.systemDefault())
    }

    val initialDisplayDateMillis = remember(selectedDate) {
        selectedDate.toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDisplayDateMillis,
        initialDisplayMode = DisplayMode.Picker
    )
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val currentDate = Date.from(Instant.now())
        selectedDate = currentDate
        onDateSelected(currentDate)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select a date: ${formatter.format(selectedDate.toInstant())}",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateSelectionBox(
                label = "Today",
                onClick = {
                    selectedDate = today
                    onDateSelected(today)
                },
                modifier = Modifier.weight(1f)
            )
            DateSelectionBox(
                label = "Yesterday",
                onClick = {
                    selectedDate = yesterday
                    onDateSelected(yesterday)
                },
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clickable { showDatePicker = true }
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Pick another",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val newCustomDate = Date.from(Instant.ofEpochMilli(millis))
                                        selectedDate = newCustomDate
                                        onDateSelected(newCustomDate)
                                        showDatePicker = false
                                    }
                                },
                                enabled = datePickerState.selectedDateMillis != null
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelectionBox(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}