package com.example.walletwiz.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.events.NotificationSettingsEvent
import com.example.walletwiz.states.NotificationSettingsState

@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsState,
    onEvent: (NotificationSettingsEvent) -> Unit
) {
    // UI components for notification settings
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Notification Settings")

        // Example switch for enabling/disabling notifications
        Switch(
            checked = state.notificationsEnabled,
            onCheckedChange = { isChecked ->
                onEvent(NotificationSettingsEvent.SetNotificationsEnabled(isChecked))
            },
        )
        Text(text = "Enable Notifications")

        if (state.notificationsEnabled) {
            // Daily reminder settings
            Switch(
                checked = state.dailyRemindersEnabled,
                onCheckedChange = { isChecked ->
                    onEvent(NotificationSettingsEvent.SetDailyReminderEnabled(isChecked))
                },
            )
            Text(text = "Enable Daily Reminders")
            // TODO: TimePickerDialog to set the reminder time
            Text(text = "Daily Reminder Time: ${state.dailyReminderTime}")

            // TODO: add switch for weekly/monthly summaries
        }
    }
}