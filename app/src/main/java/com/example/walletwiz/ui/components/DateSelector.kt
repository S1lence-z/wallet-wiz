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
    val today = Date.from(now)
    val yesterday = Date.from(now.minus(1, ChronoUnit.DAYS))
    val formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy")
    // Selected date
    var selectedDate by remember { mutableStateOf(today) }
    // Variables for the custom date
    val dateState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header text
        Text(
            text = "Select a date: ${selectedDate.toInstant().atZone(ZoneId.systemDefault()).format(formatter)}",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        // Row with date selection boxes
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Select today
            DateSelectionBox(
                label = "Today",
                onClick = {
                    selectedDate = today
                    onDateSelected(today)
                }
            )
            // Select yesterday
            DateSelectionBox(
                label = "Yesterday",
                onClick = {
                    selectedDate = yesterday
                    onDateSelected(yesterday)
                }
            )
            // Select another date
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

                // Show the date picker dialog
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val newCustomDate = Date.from(Instant.ofEpochMilli(dateState.selectedDateMillis!!))
                                selectedDate = newCustomDate
                                onDateSelected(newCustomDate)
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        },
                        content = {
                            DatePicker(
                                state = dateState,
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateSelectionBox(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 16.sp
        )
    }
}
