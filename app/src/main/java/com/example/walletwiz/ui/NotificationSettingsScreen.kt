package com.example.walletwiz.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.NotificationSettingsState

@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsState,
) {
    // UI components for notification settings
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Notification Settings")

        // Example switch for enabling/disabling notifications
        Switch(
            checked = state.notificationsEnabled,
            onCheckedChange = { isChecked ->
                Log.d("NotificationSettings", "Notifications enabled: $isChecked")
            },
        )
        Text(text = "Enable Notifications")

        // TODO: add switch for daily reminders
        // TODO: add switch for weekly/monthly summaries
    }
}