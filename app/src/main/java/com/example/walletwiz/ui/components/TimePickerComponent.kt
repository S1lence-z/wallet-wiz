package com.example.walletwiz.ui.components

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun TimePickerComponent(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val (hour, minute) = try {
        selectedTime.split(":").map { it.toInt() }
    } catch (e: Exception) {
        listOf(12, 0)
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minuteOfDay ->
            onTimeSelected(String.format("%02d:%02d", hourOfDay, minuteOfDay))
        },
        hour,
        minute,
        true
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Daily Reminder Time:")
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable { timePickerDialog.show() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = selectedTime)
        }
    }
}
