package com.example.walletwiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.walletwiz.events.NotificationSettingsEvent
import com.example.walletwiz.states.NotificationSettingsState
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.HorizontalDivider
import androidx.core.content.ContextCompat
import com.example.walletwiz.ui.components.FrequencyDropdown
import com.example.walletwiz.ui.components.TimePickerComponent

@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsState,
    onEvent: (NotificationSettingsEvent) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onEvent(NotificationSettingsEvent.SetNotificationsEnabled(true))
            }
        }
    )
    // UI components for notification settings
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Notification Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Example switch for enabling/disabling notifications
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Enable Notifications")
                    Switch(
                        checked = state.notificationsEnabled,
                        onCheckedChange = { isChecked ->
                           if (isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    when (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )) {
                                        PackageManager.PERMISSION_GRANTED -> {
                                            onEvent(NotificationSettingsEvent.SetNotificationsEnabled(true))
                                        }
                                        else -> {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                } else {
                                    onEvent(NotificationSettingsEvent.SetNotificationsEnabled(true))
                                }
                            } else {
                                onEvent(NotificationSettingsEvent.SetNotificationsEnabled(false))
                            }
                        },
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (state.notificationsEnabled) {
                    // Daily reminder settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Enable Daily Reminders")
                        Switch(
                            checked = state.dailyRemindersEnabled,
                            onCheckedChange = { isChecked ->
                                onEvent(NotificationSettingsEvent.SetDailyReminderEnabled(isChecked))
                            },
                        )
                    }
                    if (state.dailyRemindersEnabled) {
                        TimePickerComponent(
                            selectedTime = state.dailyReminderTime,
                            onTimeSelected = { time ->
                                onEvent(NotificationSettingsEvent.SetDailyReminderTime(time))
                            }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Summary notification settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Enable Summary Notifications")
                        Switch(
                            checked = state.summaryNotificationEnabled,
                            onCheckedChange = { isChecked ->
                                onEvent(NotificationSettingsEvent.SetSummaryNotificationEnabled(isChecked))
                            },
                        )
                    }
                    if (state.summaryNotificationEnabled) {
                        Text(text = "Summary Notification Frequency")
                        Spacer(modifier = Modifier.height(8.dp))
                        FrequencyDropdown(
                            selectedFrequency = state.summaryNotificationFrequency,
                            onFrequencySelected = { frequency ->
                                onEvent(NotificationSettingsEvent.SetSummaryNotificationFrequency(frequency))
                            }
                        )
                    }
                }
            }
        }
    }
}